package com.example.hrm.service;

import com.example.hrm.dto.request.SubDepartmentRequest;
import com.example.hrm.dto.response.SubDepartmentResponse;
import com.example.hrm.dto.response.SubDepartmentResponseDetail;
import com.example.hrm.entity.Department;
import com.example.hrm.entity.SubDepartment;
import com.example.hrm.exception.AppException;
import com.example.hrm.exception.ErrorCode;
import com.example.hrm.mapper.EmployeeMapper;
import com.example.hrm.repository.DepartmentRepository;
import com.example.hrm.repository.EmployeeRepository;
import com.example.hrm.repository.SubDepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class SubDepartmentService {

    private final SubDepartmentRepository subDepartmentRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    public SubDepartmentResponse createSubDepartment(SubDepartmentRequest request) {
        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        SubDepartment subDepartment = SubDepartment.builder()
                .name(request.getName())
                .description(request.getDescription())
                .department(department)
                .build();

        subDepartmentRepository.save(subDepartment);
        return toResponse(subDepartment);
    }

    public Page<SubDepartmentResponse> getAllSubDepartments(Pageable pageable) {
        return subDepartmentRepository.findByIsDeletedFalse(pageable)
                .map(this::toResponse);
    }

    public Page<SubDepartmentResponse> getSubDepartmentsByDepartment(String departmentId, Pageable pageable) {
        return subDepartmentRepository.findByDepartmentIdAndIsDeletedFalse(departmentId, pageable)
                .map(this::toResponse);
    }

    public SubDepartmentResponseDetail getSubDepartmentById(String id) {

        var subDepartment = subDepartmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SUB_DEPARTMENT_DEPARTMENT_NOT_FOUND, 404));
        var employees = employeeRepository.findBySubDepartmentId(id);

        return SubDepartmentResponseDetail.builder()
                .id(subDepartment.getId())
                .employeeResponses(employees.stream().map(employeeMapper::toResponse).toList())
                .name(subDepartment.getName())
                .departmentId(subDepartment.getDepartment().getId())
                .description(subDepartment.getDescription())
                .build();
    }

    public SubDepartmentResponse updateSubDepartment(String id, SubDepartmentRequest request) {
        SubDepartment subDepartment = subDepartmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SubDepartment not found"));

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        subDepartment.setName(request.getName());
        subDepartment.setDescription(request.getDescription());
        subDepartment.setDepartment(department);

        subDepartmentRepository.save(subDepartment);
        return toResponse(subDepartment);
    }

    public void deleteSubDepartment(String id) {
        SubDepartment subDepartment = subDepartmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SubDepartment not found"));
        subDepartment.setIsDeleted(true);
        subDepartment.setDeletedAt(java.time.LocalDateTime.now());
        subDepartmentRepository.save(subDepartment);
    }




    private SubDepartmentResponse toResponse(SubDepartment subDepartment) {
        return SubDepartmentResponse.builder()
                .id(subDepartment.getId())
                .departmentId(subDepartment.getDepartment().getId())
                .name(subDepartment.getName())
                .description(subDepartment.getDescription())
                .createdAt(subDepartment.getCreatedAt())
                .updatedAt(subDepartment.getUpdatedAt())
                .isDeleted(subDepartment.getIsDeleted())
                .deletedAt(subDepartment.getDeletedAt())
                .build();
    }
}