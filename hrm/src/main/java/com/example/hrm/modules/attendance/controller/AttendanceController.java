package com.example.hrm.modules.attendance.controller;

import com.example.hrm.modules.attendance.dto.response.AttendanceDetailResponse;
import com.example.hrm.modules.attendance.dto.response.AttendanceImportResponse;
import com.example.hrm.modules.attendance.dto.response.AttendanceListResponse;
import com.example.hrm.modules.attendance.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("${app.api-prefix}/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    /**
     * 📌 Lấy danh sách chấm công (phân trang)
     * GET /attendance?page=0&size=10
     */
    @GetMapping
    public Page<AttendanceListResponse> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return attendanceService.getAll(page, size);
    }

    /**
     * 📌 Xem chi tiết 1 bản ghi chấm công
     * GET /attendance/{id}
     */
    @GetMapping("/{id}")
    public AttendanceDetailResponse getDetail(@PathVariable String id) {
        return attendanceService.getDetail(id);
    }

    /**
     * Lấy danh sách chấm công theo subDepartment
     * GET /api/attendances/sub-department/{subDepartmentId}?page=0&size=10
     */
    @GetMapping("/sub-department/{subDepartmentId}")
    public Page<AttendanceListResponse> getAllBySubDepartment(
            @PathVariable String subDepartmentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return attendanceService.getAllBySubDepartment(page, size, subDepartmentId);
    }

    /**
     * Export attendance data to Excel file
     * @param page page number (0-based)
     * @param size page size
     * @return Excel file as byte array
     */
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportAttendance(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "1000") Integer size
    ) throws IOException {
        byte[] excelData = attendanceService.exportAttendanceToExcel(page, size);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "attendance_data.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelData);
    }

    /**
     * Import attendance data from Excel file
     * @param file Excel file to import
     * @return import result with success/error details
     */
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AttendanceImportResponse> importAttendance(@RequestParam("file") MultipartFile file) throws IOException {
        AttendanceImportResponse response = attendanceService.importAttendanceFromExcel(file);
        return ResponseEntity.ok(response);
    }
}
