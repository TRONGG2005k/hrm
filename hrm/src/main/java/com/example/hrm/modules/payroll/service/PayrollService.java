package com.example.hrm.modules.payroll.service;

import com.example.hrm.modules.attendance.dto.response.AttendanceSummaryResponse;
import com.example.hrm.modules.attendance.entity.AttendanceOTRate;
import com.example.hrm.modules.attendance.repository.AttendanceRepository;
import com.example.hrm.modules.contract.dto.response.SalaryAdjustmentResponse;
import com.example.hrm.modules.contract.entity.SalaryContract;
import com.example.hrm.modules.contract.mapper.SalaryAdjustmentMapper;
import com.example.hrm.modules.contract.repository.AllowanceRuleRepository;
import com.example.hrm.modules.contract.repository.SalaryContractRepository;
import com.example.hrm.modules.contract.service.SalaryAdjustmentService;
import com.example.hrm.modules.employee.repository.EmployeeRepository;
import com.example.hrm.modules.payroll.PayrollCalculator;
import com.example.hrm.modules.payroll.PayrollPeriodCalculator;
import com.example.hrm.modules.payroll.dto.request.PayrollRequest;
import com.example.hrm.modules.payroll.dto.response.*;
import com.example.hrm.modules.payroll.entity.Payroll;
import com.example.hrm.modules.payroll.repository.PayrollRepository;
import com.example.hrm.shared.enums.AdjustmentType;
import com.example.hrm.shared.enums.PayrollStatus;
import com.example.hrm.shared.exception.AppException;
import com.example.hrm.shared.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PayrollService {

    private final PayrollRepository payrollRepository;
    private final SalaryContractRepository salaryContractRepository;
    private final SalaryAdjustmentService salaryAdjustmentService;
    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    private final PayrollCycleService payrollCycleService;
    private final PayrollCalculator payrollCalculator;
    private final SalaryAdjustmentMapper salaryAdjustmentMapper;
    private final AllowanceRuleRepository allowanceRuleRepository;

    public PayrollResponse create(PayrollRequest request) {

        /* ===================== 1. CHUẨN BỊ DỮ LIỆU ===================== */

        var cycle = payrollCycleService.getActive();

        PayrollPeriodCalculator periodCalculator = new PayrollPeriodCalculator();
        var payrollPeriod = periodCalculator.calculate(
                cycle, request.getYear(), request.getMonth()
        );

        var employee = employeeRepository
                .findByIdAndIsDeletedFalse(request.getEmployeeId())
                .orElseThrow(() -> new AppException(
                        ErrorCode.EMPLOYEE_NOT_FOUND, 404
                ));

        var salaryContract = salaryContractRepository
                .findByEmployee_IdAndContract_IdAndIsDeletedFalse(
                        employee.getId(),
                        employee.getContracts().getFirst().getId()
                )
                .orElseThrow(() -> new AppException(
                        ErrorCode.SALARY_CONTRACT_NOT_FOUND, 404
                ));

        var attendanceList = attendanceRepository.findOTForEmployee(
                employee.getId(),
                payrollPeriod.startDate(),
                payrollPeriod.endDate()
        );

        var salaryAdjustments = salaryAdjustmentService.getForPayroll(
                employee.getId(),
                payrollPeriod.startDate(),
                payrollPeriod.endDate()
        );

        var allowance = allowanceRuleRepository.findActiveRules(
                employee.getPosition().getId(),
                employee.getSubDepartment().getId()
                );
        /* ===================== 2. TÍNH LƯƠNG ===================== */

        PayrollDetailResponse payrollDetail =
                payrollCalculator.calculatePayrollDetail(
                        salaryContract.getBaseSalary(),
                        attendanceList,
                        salaryAdjustments,
                        allowance,
                        cycle
                );

        /* ===================== 3. LƯU PAYROLL ===================== */

        Payroll payroll = Payroll.builder()
                .employee(employee)
                .month(String.format("%04d-%02d",
                        request.getYear(), request.getMonth()))
                .baseSalary(payrollDetail.baseSalaryTotal().doubleValue())
                .allowance(payrollDetail.allowance().doubleValue())
                .overtime(payrollDetail.otTotal().doubleValue())
                .bonus(payrollDetail.totalBonus().doubleValue())
                .penalty(payrollDetail.totalPenalty().doubleValue())
                .totalSalary(payrollDetail.totalSalary().doubleValue())
                .status(PayrollStatus.DRAFT)
                .createdAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        payrollRepository.save(payroll);

        /* ===================== 4. ATTENDANCE SUMMARY ===================== */

        AttendanceSummaryResponse attendanceSummary =
                AttendanceSummaryResponse.builder()
                        .expectedWorkingDays(attendanceList.size())
                        .actualWorkingDays(payrollDetail.workingDays())
                        .lateDays(attendanceList.stream()
                                .filter(a -> a.getLateMinutes() > 0)
                                .count())
                        .earlyLeaveDays(attendanceList.stream()
                                .filter(a -> a.getEarlyLeaveMinutes() > 0)
                                .count())
                        .totalOtHours(
                                (long) attendanceList.stream()
                                        .flatMap(a -> a.getAttendanceOTRates().stream())
                                        .mapToDouble(AttendanceOTRate::getOtHours)
                                        .sum()
                        )
                        .build();

        /* ===================== 5. EARNINGS ===================== */

        var bonusResponses = salaryAdjustments.stream()
                .filter(a -> a.getType() == AdjustmentType.BONUS)
                .map(salaryAdjustmentMapper::toResponse)
                .toList();

        EarningsResponse earnings = getEarningsResponse(salaryContract, payrollDetail, bonusResponses);

        /* ===================== 6. DEDUCTIONS ===================== */

        var penaltyResponses = salaryAdjustments.stream()
                .filter(a -> a.getType() == AdjustmentType.PENALTY)
                .map(salaryAdjustmentMapper::toResponse)
                .toList();

        DeductionsResponse deductions = new DeductionsResponse();
        deductions.setPenalties(penaltyResponses);
        deductions.setInsurance(null); // chưa có
        deductions.setPersonalIncomeTax(BigDecimal.ZERO);
        deductions.setAdvanceSalary(BigDecimal.ZERO);
        deductions.setTotalDeductions(payrollDetail.totalPenalty());

        /* ===================== 7. SUMMARY ===================== */

        PayrollSummaryResponse summary = new PayrollSummaryResponse();
        summary.setGrossSalary(earnings.getTotalEarnings());
        summary.setTotalDeductions(deductions.getTotalDeductions());
        summary.setNetSalary(
                earnings.getTotalEarnings()
                        .subtract(deductions.getTotalDeductions())
        );

        /* ===================== 8. PERIOD ===================== */

        PeriodResponse period =
                new PeriodResponse(request.getMonth(), request.getYear());

        /* ===================== 9. EMPLOYEE ===================== */

        EmployeePayrollResponse employeeResponse =
                EmployeePayrollResponse.builder()
                        .employeeId(employee.getId())
                        .employeeCode(employee.getCode())
                        .fullName(employee.getLastName() + " " + employee.getFirstName())
                        .subDepartment(employee.getSubDepartment().getName())
                        .build();

        /* ===================== 10. METADATA ===================== */

        PayrollMetadataResponse metadata = new PayrollMetadataResponse();
        metadata.setStatus("DRAFT");
        metadata.setVersion(1);
        metadata.setCalculatedAt(LocalDateTime.now());
        metadata.setCalculatedBy("SYSTEM");

        /* ===================== 11. RESPONSE ===================== */

        return PayrollResponse.builder()
                .payrollId(payroll.getId())
                .period(period)
                .employee(employeeResponse)
                .attendanceSummary(attendanceSummary)
                .earnings(earnings)
                .deductions(deductions)
                .summary(summary)
                .metadata(metadata)
                .build();
    }

    private static EarningsResponse getEarningsResponse(SalaryContract salaryContract, PayrollDetailResponse payrollDetail, List<SalaryAdjustmentResponse> bonusResponses) {
        EarningsResponse earnings = new EarningsResponse();
        earnings.setBaseSalary(salaryContract.getBaseSalary());
        earnings.setAllowances(salaryContract.getAllowance() );
        earnings.setOvertimePay(payrollDetail.otTotal());
        earnings.setBonuses(bonusResponses);
        earnings.setTotalEarnings(
                payrollDetail.baseSalaryTotal()
                        .add(payrollDetail.allowance())
                        .add(payrollDetail.otTotal())
                        .add(payrollDetail.totalBonus())
        );
        return earnings;
    }
}
