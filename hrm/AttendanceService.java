package com.example.hrm.modules.attendance.service;

import com.example.hrm.modules.attendance.entity.Attendance;
import com.example.hrm.modules.attendance.dto.request.AttendanceCreateRequest;
import com.example.hrm.modules.attendance.dto.response.AttendanceResponse;
import com.example.hrm.modules.attendance.repository.AttendanceRepository;
import com.example.hrm.modules.attendance.mapper.AttendanceMapper;
import com.example.hrm.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Attendance Service - Xử lý business logic cho module Attendance
 * 
 * Module: Attendance (Chấm công)
 * 
 * Chức năng:
 * - CRUD operations cho Attendance
 * - Xử lý check-in/check-out
 * - Tính toán thống kê chấm công
 * 
 * @author HRM Team
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceService {
    
    private final AttendanceRepository attendanceRepository;
    private final AttendanceMapper attendanceMapper;
    
    /**
     * Tạo mới bản ghi chấm công
     */
    @Transactional
    public AttendanceResponse createAttendance(AttendanceCreateRequest request) {
        log.info("Creating new attendance record");
        
        Attendance attendance = attendanceMapper.toEntity(request);
        Attendance saved = attendanceRepository.save(attendance);
        
        log.info("Attendance created with ID: {}", saved.getId());
        return attendanceMapper.toResponse(saved);
    }
    
    /**
     * Lấy thông tin chấm công theo ID
     */
    public AttendanceResponse getAttendance(Long id) {
        log.info("Fetching attendance with ID: {}", id);
        
        Attendance attendance = attendanceRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Attendance not found with ID: " + id));
        
        return attendanceMapper.toResponse(attendance);
    }
    
    /**
     * Lấy danh sách chấm công của nhân viên
     */
    public List<AttendanceResponse> getAttendanceByEmployeeId(Long employeeId) {
        log.info("Fetching attendance records for employee ID: {}", employeeId);
        
        return attendanceRepository.findByEmployeeId(employeeId)
            .stream()
            .map(attendanceMapper::toResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Cập nhật bản ghi chấm công
     */
    @Transactional
    public AttendanceResponse updateAttendance(Long id, AttendanceCreateRequest request) {
        log.info("Updating attendance with ID: {}", id);
        
        Attendance attendance = attendanceRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Attendance not found with ID: " + id));
        
        Attendance updated = attendanceMapper.updateEntity(request, attendance);
        Attendance saved = attendanceRepository.save(updated);
        
        log.info("Attendance updated with ID: {}", id);
        return attendanceMapper.toResponse(saved);
    }
    
    /**
     * Xóa bản ghi chấm công
     */
    @Transactional
    public void deleteAttendance(Long id) {
        log.info("Deleting attendance with ID: {}", id);
        
        if (!attendanceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Attendance not found with ID: " + id);
        }
        
        attendanceRepository.deleteById(id);
        log.info("Attendance deleted with ID: {}", id);
    }
}
