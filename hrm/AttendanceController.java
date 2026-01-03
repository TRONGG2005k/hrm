package com.example.hrm.modules.attendance.controller;

import com.example.hrm.modules.attendance.dto.request.AttendanceCreateRequest;
import com.example.hrm.modules.attendance.dto.response.AttendanceResponse;
import com.example.hrm.modules.attendance.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Attendance Controller - REST endpoints cho module Attendance
 * 
 * Module: Attendance (Chấm công)
 * Base URL: /api/v1/attendance
 * 
 * @author HRM Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
@Slf4j
public class AttendanceController {
    
    private final AttendanceService attendanceService;
    
    /**
     * POST /api/v1/attendance
     * Tạo mới bản ghi chấm công
     */
    @PostMapping
    public ResponseEntity<AttendanceResponse> createAttendance(
            @RequestBody AttendanceCreateRequest request) {
        log.info("POST /api/v1/attendance - Creating new attendance");
        
        AttendanceResponse response = attendanceService.createAttendance(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    /**
     * GET /api/v1/attendance/{id}
     * Lấy thông tin chấm công theo ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<AttendanceResponse> getAttendance(@PathVariable Long id) {
        log.info("GET /api/v1/attendance/{} - Fetching attendance", id);
        
        AttendanceResponse response = attendanceService.getAttendance(id);
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/v1/attendance/employee/{employeeId}
     * Lấy danh sách chấm công của nhân viên
     */
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<AttendanceResponse>> getAttendanceByEmployee(
            @PathVariable Long employeeId) {
        log.info("GET /api/v1/attendance/employee/{} - Fetching attendance records", employeeId);
        
        List<AttendanceResponse> responses = attendanceService.getAttendanceByEmployeeId(employeeId);
        return ResponseEntity.ok(responses);
    }
    
    /**
     * PUT /api/v1/attendance/{id}
     * Cập nhật bản ghi chấm công
     */
    @PutMapping("/{id}")
    public ResponseEntity<AttendanceResponse> updateAttendance(
            @PathVariable Long id,
            @RequestBody AttendanceCreateRequest request) {
        log.info("PUT /api/v1/attendance/{} - Updating attendance", id);
        
        AttendanceResponse response = attendanceService.updateAttendance(id, request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * DELETE /api/v1/attendance/{id}
     * Xóa bản ghi chấm công
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttendance(@PathVariable Long id) {
        log.info("DELETE /api/v1/attendance/{} - Deleting attendance", id);
        
        attendanceService.deleteAttendance(id);
        return ResponseEntity.noContent().build();
    }
}
