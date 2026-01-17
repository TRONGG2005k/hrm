package com.example.hrm.modules.payroll.service;

import com.example.hrm.modules.attendance.repository.AttendanceRepository;
import com.example.hrm.modules.contract.repository.AllowanceRuleRepository;
import com.example.hrm.modules.contract.repository.SalaryContractRepository;
import com.example.hrm.modules.contract.service.SalaryAdjustmentService;
import com.example.hrm.modules.employee.repository.EmployeeRepository;
import com.example.hrm.modules.payroll.PayrollCalculator;
import com.example.hrm.modules.payroll.PayrollPeriodCalculator;
import com.example.hrm.modules.payroll.dto.request.PayrollRequest;
import com.example.hrm.modules.payroll.dto.response.*;
import com.example.hrm.modules.payroll.entity.Payroll;
import com.example.hrm.modules.payroll.mapper.PayrollResponseMapper;
import com.example.hrm.modules.payroll.repository.PayrollRepository;
import com.example.hrm.shared.enums.PayrollStatus;
import com.example.hrm.shared.exception.AppException;
import com.example.hrm.shared.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class PayrollService {

    private final PayrollRepository payrollRepository;
    private final SalaryContractRepository salaryContractRepository;
    private final SalaryAdjustmentService salaryAdjustmentService;
    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    private final PayrollCycleService payrollCycleService;
    private final PayrollCalculator payrollCalculator;
    private final AllowanceRuleRepository allowanceRuleRepository;
    private final PayrollResponseMapper payrollResponseMapper;

    public PayrollResponse create(PayrollRequest request) {

        var cycle = payrollCycleService.getActive();
        var period = new PayrollPeriodCalculator()
                .calculate(cycle, request.getYear(), request.getMonth());

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
                period.startDate(),
                period.endDate()
        );

        var salaryAdjustments = salaryAdjustmentService.getForPayroll(
                employee.getId(),
                period.startDate(),
                period.endDate()
        );

        var allowance = allowanceRuleRepository.findActiveRules(
                employee.getPosition().getId(),
                employee.getSubDepartment().getId()
        );

        var payrollDetail = payrollCalculator.calculatePayrollDetail(
                salaryContract.getBaseSalary(),
                attendanceList,
                salaryAdjustments,
                allowance,
                cycle
        );

        Payroll payroll = Payroll.builder()
                .employee(employee)
                .month(String.format("%04d-%02d",
                        request.getYear(), request.getMonth()))
                .baseSalary(payrollDetail.baseSalaryTotal().doubleValue())
                .allowance(payrollDetail.totalAllowance().doubleValue())
                .overtime(payrollDetail.otTotal().doubleValue())
                .bonus(payrollDetail.totalBonus().doubleValue())
                .penalty(payrollDetail.totalPenalty().doubleValue())
                .totalSalary(payrollDetail.totalSalary().doubleValue())
                .status(PayrollStatus.DRAFT)
                .createdAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        payrollRepository.save(payroll);

        return PayrollResponse.builder()
                .payrollId(payroll.getId())
                .period(payrollResponseMapper.toPeriodResponse(request))
                .employee(payrollResponseMapper.toEmployeeResponse(employee))
                .attendanceSummary(
                        payrollResponseMapper.toAttendanceSummary(attendanceList, payrollDetail)
                )
                .earnings(
                        payrollResponseMapper.toEarningsResponse(
                                salaryContract, payrollDetail, salaryAdjustments
                        )
                )
                .deductions(
                        payrollResponseMapper.toDeductionsResponse(
                                salaryAdjustments, payrollDetail
                        )
                )
                .summary(
                        payrollResponseMapper.toPayrollSummary(
                                payrollResponseMapper.toEarningsResponse(
                                        salaryContract, payrollDetail, salaryAdjustments
                                ),
                                payrollResponseMapper.toDeductionsResponse(
                                        salaryAdjustments, payrollDetail
                                )
                        )
                )
                .metadata(payrollResponseMapper.toMetadata())
                .build();
    }
}


