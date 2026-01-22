package com.example.hrm.modules.employee.controller;

import com.example.hrm.modules.employee.dto.request.EmployeeRequest;
import com.example.hrm.modules.employee.dto.response.EmployeeImportResponse;
import com.example.hrm.modules.employee.dto.response.EmployeeResponse;
import com.example.hrm.modules.employee.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

import java.io.IOException;

@RestController
@RequestMapping("${app.api-prefix}/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    /**
     * Lấy danh sách tất cả nhân viên (có phân trang)
     *
     * @param page Trang (mặc định 0)
     * @param size Số lượng bản ghi trên một trang (mặc định 10)
     * @return Danh sách nhân viên
     */
    @GetMapping
    public ResponseEntity<Page<EmployeeResponse>> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<EmployeeResponse> employees = employeeService.getAllEmployees(page, size);
        return ResponseEntity.ok(employees);
    }

    /**
     * Lấy thông tin chi tiết nhân viên theo ID
     *
     * @param id ID của nhân viên
     * @return Thông tin nhân viên
     */
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable String id) {
        EmployeeResponse employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }

    /**
     * Tạo nhân viên mới
     *
     * @param request Dữ liệu nhân viên từ request (hỗ trợ upload avatar)
     * @return Nhân viên vừa tạo
     */
    @PostMapping
    public ResponseEntity<EmployeeResponse> createEmployee(@RequestBody @Valid EmployeeRequest request) {
        EmployeeResponse employee = employeeService.createEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(employee);
    }

    /**
     * Cập nhật thông tin nhân viên
     *
     * @param id ID của nhân viên cần cập nhật
     * @param request Dữ liệu cập nhật (hỗ trợ upload avatar mới)
     * @return Nhân viên sau khi cập nhật
     */
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @PathVariable String id,
            @RequestBody @Valid EmployeeRequest request) {
        EmployeeResponse employee = employeeService.updateEmployee(id, request);
        return ResponseEntity.ok(employee);
    }

    /**
     * Xóa mềm nhân viên (đánh dấu là đã xóa)
     *
     * @param id ID của nhân viên cần xóa
     * @return Trạng thái xóa thành công
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable String id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Export employee data to Excel file
     * @param page page number (0-based)
     * @param size page size
     * @return Excel file as byte array
     */
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportEmployees(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "1000") Integer size
    ) throws IOException {
        byte[] excelData = employeeService.exportEmployeesToExcel(page, size);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "employee_data.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelData);
    }

    /**
     * Import employee data from Excel file
     * @param file Excel file to import
     * @return import result with success/error details
     */
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EmployeeImportResponse> importEmployees(@RequestParam("file") MultipartFile file) throws IOException {
        EmployeeImportResponse response = employeeService.importEmployeesFromExcel(file);
        return ResponseEntity.ok(response);
    }
}
