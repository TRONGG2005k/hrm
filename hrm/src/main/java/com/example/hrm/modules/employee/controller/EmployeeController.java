package com.example.hrm.modules.employee.controller;

import com.example.hrm.modules.employee.dto.request.EmployeeRequest;
import com.example.hrm.modules.employee.dto.response.EmployeeResponse;
import com.example.hrm.modules.employee.service.EmployeeService;
import com.example.hrm.modules.employee.excel.EmployeeExcelService;
import com.example.hrm.modules.employee.excel.dto.ExcelImportResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

@RestController
@RequestMapping("${app.api-prefix}/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;
    private final EmployeeExcelService employeeExcelService;

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
     * Import nhân viên từ file Excel
     *
     * @param file File Excel chứa dữ liệu nhân viên
     * @return Kết quả import
     */
    @PostMapping("/import")
    public ResponseEntity<ExcelImportResult> importEmployees(@RequestParam("file") MultipartFile file) {
        ExcelImportResult result = employeeExcelService.importEmployees(file);
        return ResponseEntity.ok(result);
    }


}
