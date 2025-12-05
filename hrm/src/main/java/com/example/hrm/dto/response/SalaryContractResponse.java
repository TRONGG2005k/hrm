package com.example.hrm.dto.response;

import com.example.hrm.enums.SalaryContractStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SalaryContractResponse {

    String id;

    String employeeId;

    String contractId;

    Double baseSalary;

    Double allowance;

    Double salaryCoefficient;

    LocalDate effectiveDate;

    SalaryContractStatus status;

    LocalDateTime createdAt;

    LocalDateTime updatedAt;

    Boolean isDeleted;

    LocalDateTime deletedAt;
}
