package com.example.hrm.controller;

import com.example.hrm.dto.response.AttendanceDetailResponse;
import com.example.hrm.dto.response.AttendanceListResponse;
import com.example.hrm.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

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
}
