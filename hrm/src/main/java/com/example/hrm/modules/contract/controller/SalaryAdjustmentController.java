package com.example.hrm.modules.contract.controller;

import com.example.hrm.modules.contract.dto.request.SalaryAdjustmentRequest;
import com.example.hrm.modules.contract.dto.response.SalaryAdjustmentResponse;
import com.example.hrm.modules.contract.service.SalaryAdjustmentService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${app.api-prefix}/salary-adjustment")
@RequiredArgsConstructor
public class SalaryAdjustmentController {

    private final SalaryAdjustmentService adjustmentService;

    @PostMapping
    public SalaryAdjustmentResponse create(@RequestBody SalaryAdjustmentRequest request) {
        return adjustmentService.create(request);
    }

    @GetMapping("/employee/{employeeId}/month/{month}")
    public Page<SalaryAdjustmentResponse> getAllByEmployeeAndMonth(
            @PathVariable String employeeId,
            @PathVariable String month,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return adjustmentService.getAllByEmployeeAndMonth(employeeId, month, pageable);
    }

    @PutMapping("/{id}")
    public SalaryAdjustmentResponse update(@PathVariable String id, @RequestBody SalaryAdjustmentRequest request) {
        return adjustmentService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        adjustmentService.delete(id);
    }
}
