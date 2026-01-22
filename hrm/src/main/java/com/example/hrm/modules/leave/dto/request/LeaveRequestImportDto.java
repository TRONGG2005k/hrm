package com.example.hrm.modules.leave.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * DTO for importing leave request data from Excel
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LeaveRequestImportDto {
    String employeeCode;
    String startDate; // format: YYYY-MM-DD
    String endDate; // format: YYYY-MM-DD
    String type; // ANNUAL, UNPAID, SICK, etc.
    String status; // PENDING, APPROVED, REJECTED, CANCELLED
    String reason;

    // For validation tracking
    Integer rowNumber;
}
