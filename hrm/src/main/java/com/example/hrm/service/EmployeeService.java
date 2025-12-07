package com.example.hrm.service;

import com.example.hrm.dto.response.FileAttachmentResponse;
import com.example.hrm.entity.FileAttachment;
import com.example.hrm.enums.RefType;
// import com.example.hrm.mapper.AddressMapper;
import com.example.hrm.mapper.FileAttachmentMapper;
import com.example.hrm.repository.AddressRepository;
import com.example.hrm.repository.FileAttachmentRepository;
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
import com.example.hrm.repository.SubDepartmentRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final AddressRepository addressRepository;
    private final FileAttachmentRepository fileAttachmentRepository;
    private final SubDepartmentRepository subDepartmentRepository;

    public Page<EmployeeResponse> getAllEmployees(int page, int size) {
        var employeePage = employeeRepository.findByIsDeletedFalse(PageRequest.of(page, size));
        return employeePage.map(employeeMapper::toResponse);
    }

    public EmployeeResponse getEmployeeById(String id) {
        var employee = employeeRepository.findEmployeeWithFiles(id)
                .orElseThrow(
                        () -> new AppException(ErrorCode.EMPLOYEE_NOT_FOUND, 404, "Employee not found with id: " + id
                                + "or has been deleted"));
        List<FileAttachment> fileAttachments = fileAttachmentRepository
                .findByRefTypeAndRefIdAndIsDeletedFalse(RefType.EMPLOYEE.getValue(), employee.getId());
        List<FileAttachmentResponse> fileAttachmentResponses = fileAttachments.stream().map(
                FileAttachmentMapper.INSTANCE::toResponse).toList();
        var employeeResponse = employeeMapper.toResponse(employee);
        employeeResponse.setFileAttachmentResponses(fileAttachmentResponses);
        return employeeResponse;
    }

    @Transactional
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        Employee employee = employeeMapper.toEntity(request);
        var address = addressRepository.findByIdAndIsDeletedFalse(request.getAddress()).orElseThrow(
                () -> new AppException(ErrorCode.ADDRESS_NOT_FOUND, 404, "address not found "));
        employee.setAddress(address);
        var subDepartment = subDepartmentRepository.findByIdAndIsDeletedFalse(request.getSubDepartmentId()).orElseThrow(
                () -> new AppException(ErrorCode.SUB_DEPARTMENT_NOT_FOUND, 404, "sub department not found "));
        employee.setSubDepartment(subDepartment);
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
        var address = addressRepository.findByIdAndIsDeletedFalse(request.getAddress()).orElseThrow(
                () -> new AppException(ErrorCode.ADDRESS_NOT_FOUND, 404, "address not found "));
        employee.setAddress(address);
        var subDepartment = subDepartmentRepository.findByIdAndIsDeletedFalse(request.getSubDepartmentId()).orElseThrow(
                () -> new AppException(ErrorCode.SUB_DEPARTMENT_NOT_FOUND, 404, "sub department not found "));
        employee.setSubDepartment(subDepartment);
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
