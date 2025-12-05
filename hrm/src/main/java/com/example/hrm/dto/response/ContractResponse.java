package com.example.hrm.dto.response;

import com.example.hrm.enums.ContractType;
import com.example.hrm.enums.ContractStatus;
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
public class ContractResponse {

    String id;

    String employeeId;

    String code;

    ContractType type;

    LocalDate signDate;

    LocalDate startDate;

    LocalDate endDate;

    Double baseSalary;

    ContractStatus status;

    String note;

    LocalDateTime createdAt;

    LocalDateTime updatedAt;

    Boolean isDeleted;

    LocalDateTime deletedAt;
}
