package com.example.hrm.controller;

import com.example.hrm.dto.request.SubDepartmentRequest;
import com.example.hrm.dto.response.SubDepartmentResponse;
import com.example.hrm.service.SubDepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("${app.api-prefix}/sub-departments")
@RequiredArgsConstructor
public class SubDepartmentController {

    private final SubDepartmentService subDepartmentService;

    @PostMapping
    public SubDepartmentResponse create(@RequestBody SubDepartmentRequest request) {
        return subDepartmentService.createSubDepartment(request);
    }

    @GetMapping
    public Page<SubDepartmentResponse> getAll(Pageable pageable) {
        return subDepartmentService.getAllSubDepartments(pageable);
    }

    @GetMapping("/department/{departmentId}")
    public Page<SubDepartmentResponse> getByDepartment(@PathVariable String departmentId, Pageable pageable) {
        return subDepartmentService.getSubDepartmentsByDepartment(departmentId, pageable);
    }

    @GetMapping("/{id}")
    public Optional<SubDepartmentResponse> getById(@PathVariable String id) {
        return subDepartmentService.getSubDepartmentById(id);
    }

    @PutMapping("/{id}")
    public SubDepartmentResponse update(@PathVariable String id, @RequestBody SubDepartmentRequest request) {
        return subDepartmentService.updateSubDepartment(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        subDepartmentService.deleteSubDepartment(id);
    }
}