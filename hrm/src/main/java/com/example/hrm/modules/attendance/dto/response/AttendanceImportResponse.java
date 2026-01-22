package com.example.hrm.modules.attendance.dto.response;

import com.example.hrm.modules.payroll.dto.response.ImportErrorDto;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

/**
 * Response DTO for attendance import operation
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttendanceImportResponse {
    Integer totalRows;
    Integer successCount;
    Integer errorCount;
    List<ImportErrorDto> errors;
    List<String> importedAttendanceIds;
}
