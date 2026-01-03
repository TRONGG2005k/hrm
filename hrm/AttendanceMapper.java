package com.example.hrm.modules.attendance.mapper;

import com.example.hrm.modules.attendance.entity.Attendance;
import com.example.hrm.modules.attendance.dto.request.AttendanceCreateRequest;
import com.example.hrm.modules.attendance.dto.response.AttendanceResponse;
import org.springframework.stereotype.Component;

/**
 * Attendance Mapper - Chuyển đổi giữa Entity, DTO và Response Objects
 * 
 * Module: Attendance (Chấm công)
 * 
 * Chức năng:
 * - Entity -> Response DTO
 * - Request DTO -> Entity
 * - Cập nhật Entity từ Request DTO
 * 
 * @author HRM Team
 * @version 1.0
 */
@Component
public class AttendanceMapper {
    
    /**
     * Chuyển đổi Attendance Entity thành Response DTO
     */
    public AttendanceResponse toResponse(Attendance entity) {
        if (entity == null) {
            return null;
        }
        
        return AttendanceResponse.builder()
                .id(entity.getId())
                .employeeId(entity.getEmployeeId())
                .checkInTime(entity.getCheckInTime())
                .checkOutTime(entity.getCheckOutTime())
                .attendanceDate(entity.getAttendanceDate())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
    
    /**
     * Chuyển đổi Request DTO thành Attendance Entity
     */
    public Attendance toEntity(AttendanceCreateRequest request) {
        if (request == null) {
            return null;
        }
        
        return Attendance.builder()
                .employeeId(request.getEmployeeId())
                .checkInTime(request.getCheckInTime())
                .checkOutTime(request.getCheckOutTime())
                .attendanceDate(request.getAttendanceDate())
                .status(request.getStatus())
                .build();
    }
    
    /**
     * Cập nhật Attendance Entity từ Request DTO
     */
    public Attendance updateEntity(AttendanceCreateRequest request, Attendance entity) {
        if (request == null) {
            return entity;
        }
        
        if (request.getEmployeeId() != null) {
            entity.setEmployeeId(request.getEmployeeId());
        }
        if (request.getCheckInTime() != null) {
            entity.setCheckInTime(request.getCheckInTime());
        }
        if (request.getCheckOutTime() != null) {
            entity.setCheckOutTime(request.getCheckOutTime());
        }
        if (request.getAttendanceDate() != null) {
            entity.setAttendanceDate(request.getAttendanceDate());
        }
        if (request.getStatus() != null) {
            entity.setStatus(request.getStatus());
        }
        
        return entity;
    }
}
