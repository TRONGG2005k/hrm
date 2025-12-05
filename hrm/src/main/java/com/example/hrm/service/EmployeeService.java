package com.example.hrm.service;

import org.springframework.stereotype.Service;
import com.example.hrm.mapper.EmployeeMapper;
import com.example.hrm.repository.EmployeeRepository;
import com.example.hrm.entity.Employee;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import com.example.hrm.dto.request.EmployeeRequest;
import com.example.hrm.dto.response.EmployeeResponse;
import com.example.hrm.exception.AppException;
import com.example.hrm.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    public Page<EmployeeResponse> getAllEmployees(int page, int size) {
        var employeePage = employeeRepository.findByIsDeletedFalse(PageRequest.of(page, size));
        return employeePage.map(employeeMapper::toResponse);
    }

    public EmployeeResponse getEmployeeById(String id) {
        var employee = employeeRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(
                        () -> new AppException(ErrorCode.EMPLOYEE_NOT_FOUND, 404, "Employee not found with id: " + id
                                + "or has been deleted"));
        return employeeMapper.toResponse(employee);
    }

    @Transactional
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        Employee employee = employeeMapper.toEntity(request);
        if (employee == null) {
            throw new AppException(ErrorCode.INVALID_INPUT, 400, "Dữ liệu nhân viên không hợp lệ");
        }
        var savedEmployee = employeeRepository.save(employee);
        return employeeMapper.toResponse(savedEmployee);
    }

    @Transactional
    public EmployeeResponse updateEmployee(String id, EmployeeRequest request) {
        var employee = employeeRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(
                        () -> new AppException(ErrorCode.EMPLOYEE_NOT_FOUND, 404, "Employee not found with id: " + id
                                + "or has been deleted"));
        employeeMapper.updateEntity(request, employee);
        @SuppressWarnings("null")
        Employee updatedEmployee = employeeRepository.save(employee);
        return employeeMapper.toResponse(updatedEmployee);
    }

    @Transactional
    public void deleteEmployee(String id) {
        var employee = employeeRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(
                        () -> new AppException(ErrorCode.EMPLOYEE_NOT_FOUND, 404, "Employee not found with id: " + id));
        employee.setIsDeleted(true);
        employeeRepository.save(employee);
    }
}
