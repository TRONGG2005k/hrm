package com.example.hrm.dto.request;

import com.example.hrm.enums.ContractType;
import com.example.hrm.enums.ContractStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContractRequest {

    @NotBlank(message = "Mã nhân viên không được để trống")
    String employeeId;

    @NotBlank(message = "Mã hợp đồng không được để trống")
    String code;

    @NotNull(message = "Loại hợp đồng không được để trống")
    ContractType type;

    LocalDate signDate;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    LocalDate startDate;

    LocalDate endDate;

    Double baseSalary;

    @NotNull(message = "Trạng thái không được để trống")
    ContractStatus status;

    String note;
}
