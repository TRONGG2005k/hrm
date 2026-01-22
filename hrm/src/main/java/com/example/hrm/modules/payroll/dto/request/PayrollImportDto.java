package com.example.hrm.modules.payroll.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

/**
 * DTO for importing payroll data from Excel
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PayrollImportDto {
    String employeeCode;
    String month; // format: YYYY-MM
    BigDecimal baseSalary;
    BigDecimal allowance;
    Double overtime;
    BigDecimal bonus;
    BigDecimal penalty;
    BigDecimal unpaidLeave;
    String note;

    // For validation tracking
    Integer rowNumber;
}
