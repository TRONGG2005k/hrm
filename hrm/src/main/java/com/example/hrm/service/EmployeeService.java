package com.example.hrm.service;

import org.springframework.stereotype.Service;

import com.example.hrm.mapper.EmployeeMapper;
import com.example.hrm.repository.EmployeeRepository;
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
        var employeePage = employeeRepository.findAll(PageRequest.of(page, size));
        return employeePage.map(employeeMapper::toResponse);
    }

    public EmployeeResponse getEmployeeById(String id) {
        var employee = employeeRepository.findById(id)
                .orElseThrow(
                        () -> new AppException(ErrorCode.EMPLOYEE_NOT_FOUND, 404, "Employee not found with id: " + id));
        return employeeMapper.toResponse(employee);
    }

    public EmployeeResponse createEmployee(EmployeeRequest request) {
        var employee = employeeMapper.toEntity(request);
        var savedEmployee = employeeRepository.save(employee);
        return employeeMapper.toResponse(savedEmployee);
    }

    public EmployeeResponse updatEmployee(String id, EmployeeRequest request) {
        var employee = employeeRepository.findById(id)
                .orElseThrow(
                        () -> new AppException(ErrorCode.EMPLOYEE_NOT_FOUND, 404, "Employee not found with id: " + id));
        employeeMapper.updateEntity(request, employee);
        var updatedEmployee = employeeRepository.save(employee);
        return employeeMapper.toResponse(updatedEmployee);
    }

    public void deleteEmployee(String id) {
        var employee = employeeRepository.findById(id)
                .orElseThrow(
                        () -> new AppException(ErrorCode.EMPLOYEE_NOT_FOUND, 404, "Employee not found with id: " + id));
        employee.setIsDeleted(true);
        employeeRepository.save(employee);
    }
}
