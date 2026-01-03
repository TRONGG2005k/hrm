package com.example.hrm.modules.attendance.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * AttendanceCreateRequest DTO
 * 
 * Module: Attendance (Chấm công)
 * 
 * Dùng để nhận dữ liệu từ client khi tạo/cập nhật bản ghi chấm công
 * 
 * @author HRM Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceCreateRequest {
    
    private Long employeeId;
    
    private LocalDateTime checkInTime;
    
    private LocalDateTime checkOutTime;
    
    private String attendanceDate;
    
    private String status;
    
    // Validation messages
    public boolean isValid() {
        return employeeId != null && !attendanceDate.isBlank();
    }
}
