package com.example.hrm.modules.payroll.service;

import com.example.hrm.modules.payroll.dto.request.PayrollRequest;
import com.example.hrm.modules.payroll.dto.response.EmployeePayrollResponse;
import com.example.hrm.modules.payroll.dto.response.PayrollResponse;
import com.example.hrm.modules.penalty.entity.AttendancePenalty;
import com.example.hrm.shared.exception.AppException;
import com.example.hrm.shared.exception.ErrorCode;
import com.example.hrm.modules.payroll.PayrollPeriodCalculator;
import com.example.hrm.modules.penalty.repository.AttendancePenaltyRepository;
import com.example.hrm.modules.employee.repository.EmployeeRepository;
import com.example.hrm.modules.payroll.repository.PayrollCycleRepository;
import com.example.hrm.modules.payroll.repository.PayrollRepository;
import com.example.hrm.modules.contract.repository.SalaryAdjustmentRepository;
import com.example.hrm.modules.contract.repository.SalaryContractRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

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

    public PayrollResponse create(PayrollRequest request) {
        // Implementation for creating payroll

        var cycle = payrollCycleRepository.findByActiveTrue()
                .orElseThrow(() -> new AppException(
                        ErrorCode.PAYROLL_CYCLE_NOT_FOUND,
                        404,
                        "Chưa cấu hình chu kỳ lương"));
        PayrollPeriodCalculator payrollPeriodCalculator = new PayrollPeriodCalculator();
        var payrollPeriod = payrollPeriodCalculator.calculate(cycle, request.getYear(), request.getMonth());
        
        var employee = employeeRepository.findByIdAndIsDeletedFalse(request.getEmployeeId())
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_FOUND, 404));
        EmployeePayrollResponse employeePayroll = EmployeePayrollResponse.builder()
                .employeeId(employee.getId())
                .fullName(employee.getLastName() + " " + employee.getFirstName())
                .subDepartment(employee.getSubDepartment().getName())
                .build();

        var salaryContract = salaryContractRepository
                .findByEmployee_IdAndContract_IdAndIsDeletedFalse(employee.getId(),
                        employee.getContracts().getFirst().getId())
                .orElseThrow(() -> new AppException(ErrorCode.SALARY_CONTRACT_NOT_FOUND, 404));

        List<AttendancePenalty> attendancePenalties = attendancePenaltyRepository
                .findAllByEmployeeAndWorkDateBetween(request.getEmployeeId(), payrollPeriod.startDate(), payrollPeriod.endDate());
        
        
        
        return null;
    }

}
