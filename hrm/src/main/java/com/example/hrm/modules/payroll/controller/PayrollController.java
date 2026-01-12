package com.example.hrm.modules.payroll.controller;


import com.example.hrm.modules.payroll.dto.request.PayrollRequest;
import com.example.hrm.modules.payroll.dto.response.PayrollResponse;
import com.example.hrm.modules.payroll.service.PayrollService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

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
        PayrollResponse response = payrollService.create(request); // Tạm thời dùng create, về sau dùng get
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
