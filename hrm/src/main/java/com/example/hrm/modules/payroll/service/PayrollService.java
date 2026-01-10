package com.example.hrm.modules.payroll.service;

import com.example.hrm.modules.payroll.PayrollCalculator;
import com.example.hrm.modules.payroll.dto.request.PayrollRequest;
import com.example.hrm.modules.payroll.dto.response.EmployeePayrollResponse;
import com.example.hrm.modules.payroll.dto.response.PayrollResponse;
import com.example.hrm.shared.exception.AppException;
import com.example.hrm.shared.exception.ErrorCode;
import com.example.hrm.modules.payroll.PayrollPeriodCalculator;
import com.example.hrm.modules.penalty.PenaltyAmountCalculator;
import com.example.hrm.modules.penalty.dto.response.AttendancePenaltyResult;
import com.example.hrm.modules.penalty.repository.AttendancePenaltyRepository;
import com.example.hrm.modules.employee.repository.EmployeeRepository;
import com.example.hrm.modules.payroll.repository.PayrollCycleRepository;
import com.example.hrm.modules.payroll.repository.PayrollRepository;
import com.example.hrm.modules.attendance.repository.AttendanceRepository;
import com.example.hrm.modules.contract.repository.SalaryAdjustmentRepository;
import com.example.hrm.modules.contract.repository.SalaryContractRepository;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class PayrollService {
    private final PayrollRepository payrollRepository;
    private final SalaryContractRepository salaryContractRepository;
    private final SalaryAdjustmentRepository salaryAdjustmentRepository;
    private final AttendancePenaltyRepository attendancePenaltyRepository;
    private final EmployeeRepository employeeRepository;
    private final PayrollCycleRepository payrollCycleRepository;
    private final AttendanceRepository attendanceRepository;
    private final PenaltyAmountCalculator penaltyAmountCalculator;
    private final PayrollCycleService payrollCycleService;
    private final PayrollCalculator payrollCalculator;

    public PayrollResponse create(PayrollRequest request) {
        /*
         * 
         * lấy chu kỳ lương
         */

        var cycle = payrollCycleService.getActive();

         /*
         *
         * tính kỳ lương
         */
        PayrollPeriodCalculator payrollPeriodCalculator = new PayrollPeriodCalculator();

        var payrollPeriod = payrollPeriodCalculator.calculate(cycle, request.getYear(), request.getMonth());

        /*
         * 
         * lấy thông tin nhân viên
         */
        var employee = employeeRepository.findByIdAndIsDeletedFalse(request.getEmployeeId())
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_FOUND, 404));
        
        /*
         * // lấy hợp đồng lương
         */
        var salaryContract = salaryContractRepository
                .findByEmployee_IdAndContract_IdAndIsDeletedFalse(employee.getId(),
                        employee.getContracts().getFirst().getId())
                .orElseThrow(() -> new AppException(ErrorCode.SALARY_CONTRACT_NOT_FOUND, 404));


        /*
         * 
         * lấy danh sách chấm công trong kỳ
         */
        var attendanceList = attendanceRepository
                .findOTForEmployee(
                        employee.getId(), payrollPeriod.startDate(), payrollPeriod.endDate());

         /*
         * 
         * tinh lương
         */
        var salaryPerDay = payrollCalculator.calculateSalaryPerDay(
                salaryContract.getBaseSalary(), cycle);

        BigDecimal sumSalary = BigDecimal.ZERO;
        for (var attendance : attendanceList) {

            AttendancePenaltyResult penaltyResult = penaltyAmountCalculator.applyAttendancePenaltyRule(attendance);

            // Mất ngày công
            if (penaltyResult.isVoidBaseSalary()) {
                continue;
            }

            BigDecimal netDailySalary = payrollCalculator.calculateNetDailySalary(salaryPerDay, penaltyResult);

            sumSalary = sumSalary.add(netDailySalary);

            // tinh OT
            if (!penaltyResult.isVoidOvertime()) {
                for (var otRate : attendance.getAttendanceOTRates()) {

                    BigDecimal otMoney =
                            payrollCalculator.calculateOt(otRate, salaryPerDay);

                    sumSalary = sumSalary.add(otMoney);
                }
            }
        }

        /*
         * 
         * tạo response
         */
        EmployeePayrollResponse employeePayroll = EmployeePayrollResponse.builder()
                .employeeId(employee.getId())
                .fullName(employee.getLastName() + " " + employee.getFirstName())
                .subDepartment(employee.getSubDepartment().getName())
                .build();
        return null;
    }

}
