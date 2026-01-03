package com.example.hrm.dto.request;

import com.example.hrm.enums.ContractType;
import com.example.hrm.enums.ContractStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

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

    @NotNull(message = "Ngày ký hợp đồng không được để trống")
    LocalDate signDate;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    LocalDate startDate;

    LocalDate endDate;

    @NotNull(message = "Trạng thái hợp đồng không được để trống")
    ContractStatus status;

    String note;

    @NotNull(message = "Thông tin lương không được để trống")
    SalaryContractRequest salaryContract;
}
