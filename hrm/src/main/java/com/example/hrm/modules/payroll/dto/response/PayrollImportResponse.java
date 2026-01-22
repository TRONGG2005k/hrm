package com.example.hrm.modules.payroll.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

/**
 * Response DTO for payroll import operation
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PayrollImportResponse {
    Integer totalRows;
    Integer successCount;
    Integer errorCount;
    List<ImportErrorDto> errors;
    List<String> importedPayrollIds;
}
