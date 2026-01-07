package com.example.hrm.modules.contract.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContractListResponse {
    String id;
    String contractCode;
    String contractType;
    String status;
    LocalDate startDate;
    LocalDate endDate;

    String employeeId;
    String employeeCode;
    String employeeName;
}

