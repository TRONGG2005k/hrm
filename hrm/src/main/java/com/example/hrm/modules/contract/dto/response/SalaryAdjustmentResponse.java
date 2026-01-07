package com.example.hrm.modules.contract.dto.response;

import com.example.hrm.shared.enums.AdjustmentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SalaryAdjustmentResponse {

    String id;

    String employeeId;

    String month;

    AdjustmentType type;

    Double amount;

    String description;

    LocalDateTime createdAt;

    Boolean isDeleted;

    LocalDateTime deletedAt;
}
