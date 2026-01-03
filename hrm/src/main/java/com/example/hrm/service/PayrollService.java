package com.example.hrm.service;

import com.example.hrm.dto.request.PayrollRequest;
import com.example.hrm.dto.response.EmployeePayrollResponse;
import com.example.hrm.dto.response.PayrollResponse;
import com.example.hrm.entity.AttendancePenalty;
import com.example.hrm.exception.AppException;
import com.example.hrm.exception.ErrorCode;
import com.example.hrm.payroll.PayrollPeriodCalculator;
import com.example.hrm.repository.AttendancePenaltyRepository;
import com.example.hrm.repository.EmployeeRepository;
import com.example.hrm.repository.PayrollCycleRepository;
import com.example.hrm.repository.PayrollRepository;
import com.example.hrm.repository.SalaryAdjustmentRepository;
import com.example.hrm.repository.SalaryContractRepository;
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
