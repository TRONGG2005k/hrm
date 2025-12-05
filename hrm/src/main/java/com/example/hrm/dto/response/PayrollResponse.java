package com.example.hrm.dto.response;

import com.example.hrm.enums.PayrollStatus;
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
public class PayrollResponse {

    String id;

    String employeeId;

    String month;

    Double baseSalary;

    Double allowance;

    Double overtime;

    Double bonus;

    Double penalty;

    Double unpaidLeave;

    Double totalSalary;

    PayrollStatus status;

    LocalDateTime createdAt;

    LocalDateTime updatedAt;

    Boolean isDeleted;

    LocalDateTime deletedAt;
}
