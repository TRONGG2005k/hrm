package com.example.hrm.modules.attendance.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * AttendanceResponse DTO
 * 
 * Module: Attendance (Chấm công)
 * 
 * Dùng để trả về dữ liệu chấm công cho client
 * 
 * @author HRM Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceResponse {
    
    private Long id;
    
    private Long employeeId;
    
    private LocalDateTime checkInTime;
    
    private LocalDateTime checkOutTime;
    
    private String attendanceDate;
    
    private String status;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
