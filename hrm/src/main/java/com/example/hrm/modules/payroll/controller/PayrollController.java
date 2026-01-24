package com.example.hrm.modules.payroll.controller;


import com.example.hrm.modules.payroll.dto.request.PayrollApprovalRequest;
import com.example.hrm.modules.payroll.dto.request.PayrollRequest;
import com.example.hrm.modules.payroll.dto.response.*;
import com.example.hrm.modules.payroll.service.PayrollService;
import com.example.hrm.shared.enums.PayrollStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("${app.api-prefix}/payroll")
@RequiredArgsConstructor
public class PayrollController {

    private final PayrollService payrollService;

    /**
     * Tạo và tính lương cho một nhân viên trong kỳ nhất định
     * @param request Thông tin yêu cầu tính lương: employeeId, month, year
     * @return PayrollResponse chi tiết
     */
    @PostMapping("/create")
    public ResponseEntity<PayrollResponse> createPayroll(@Valid @RequestBody PayrollRequest request) {
        PayrollResponse response = payrollService.create(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<List<PayrollListItemResponse>> createPayrollForAllEmployee(
            @RequestParam int month,
            @RequestParam int year
    ) {
        List<PayrollListItemResponse> response = payrollService.createForAllEmployees(month, year);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<PayrollListItemResponse>> getPayroll(
            @RequestParam Integer page,
            @RequestParam Integer size
    ) {
        // Sau này có thể tạo service getPayrollByEmployeeAndMonth

        Page<PayrollListItemResponse> response = payrollService.getAll(page, size); // Tạm thời dùng create, về sau dùng get
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    /**
     * Lấy chi tiết lương của một nhân viên trong một tháng
     * @param employeeId ID nhân viên
     * @param year Năm
     * @param month Tháng
     */
    @GetMapping("/{employeeId}")
    public ResponseEntity<PayrollResponse> getPayroll(
            @PathVariable String employeeId,
            @RequestParam Integer year,
            @RequestParam Integer month
    ) {
        // Sau này có thể tạo service getPayrollByEmployeeAndMonth
        PayrollRequest request = new PayrollRequest(employeeId, month, year);
        PayrollResponse response = payrollService.getDetailByEmployee(employeeId, month, year); // Tạm thời dùng create, về sau dùng get
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Duyệt hoặc từ chối bảng lương hàng loạt
     * @param request Thông tin duyệt bảng lương: month, year, status, comment
     * @return ApprovedPayrollListResponse danh sách bảng lương đã duyệt
     */
    @PostMapping("/approval")
    public ResponseEntity<ApprovedPayrollListResponse> salaryApproval(@Valid @RequestBody PayrollApprovalRequest request) throws JsonProcessingException {
        ApprovedPayrollListResponse response = payrollService.salaryApproval(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Lấy danh sách bảng lương theo trạng thái
     * @param month Tháng
     * @param year Năm
     * @param status Trạng thái bảng lương
     * @return PayrollListResponse danh sách bảng lương
     */
    @GetMapping("/list")
    public ResponseEntity<PayrollListResponse> getPayrollList(
            @RequestParam Integer month,
            @RequestParam Integer year,
            @RequestParam PayrollStatus status
    ) {
        PayrollApprovalRequest request = new PayrollApprovalRequest(month, year, null, status);
        PayrollListResponse response = payrollService.getPayrollList(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<Page<PayrollListItemResponse>> getPayrollByEmployee(
            @PathVariable String employeeId,
            @RequestParam Integer page,
            @RequestParam Integer size
    ) {
        Page<PayrollListItemResponse> response =
                payrollService.getAllByEmployeeId(employeeId, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/month")
    public ResponseEntity<Page<PayrollListItemResponse>> getPayrollByMonth(
            @RequestParam Integer month,
            @RequestParam Integer year,
            @RequestParam Integer page,
            @RequestParam Integer size
    ) {
        Page<PayrollListItemResponse> response =
                payrollService.getAllByMouth(page, size, month, year);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/detail/{payrollId}")
    public ResponseEntity<PayrollResponse> getPayrollById(
            @PathVariable String payrollId
    ) {
        PayrollResponse response = payrollService.getById(payrollId);
        return ResponseEntity.ok(response);
    }

}
