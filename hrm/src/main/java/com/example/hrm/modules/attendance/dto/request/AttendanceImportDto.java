package com.example.hrm.modules.attendance.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * DTO for importing attendance data from Excel
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttendanceImportDto {
    String employeeCode;
    String checkInTime; // format: YYYY-MM-DD HH:mm:ss
    String checkOutTime; // format: YYYY-MM-DD HH:mm:ss
    String workDate; // format: YYYY-MM-DD
    String status;
    String evaluation;

    // For validation tracking
    Integer rowNumber;
}
