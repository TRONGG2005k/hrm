package com.example.hrm.modules.organization.controller;

import com.example.hrm.modules.organization.dto.request.DepartmentRequest;
import com.example.hrm.modules.organization.dto.response.DepartmentResponse;
import com.example.hrm.modules.organization.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("${app.api-prefix}/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    public DepartmentResponse create(@RequestBody DepartmentRequest request) {
        return departmentService.createDepartment(request);
    }

    @GetMapping
    public Page<DepartmentResponse> getAll(Pageable pageable) {
        return departmentService.getAllDepartments(pageable);
    }

    @GetMapping("/{id}")
    public DepartmentResponse getById(@PathVariable String id) {
        return departmentService.getDepartmentById(id);
    }

    @PutMapping("/{id}")
    public DepartmentResponse update(@PathVariable String id, @RequestBody DepartmentRequest request) {
        return departmentService.updateDepartment(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        departmentService.deleteDepartment(id);
    }
}