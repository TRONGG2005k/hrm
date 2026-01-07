package com.example.hrm.modules.contract.dto.request;

import com.example.hrm.shared.enums.AdjustmentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SalaryAdjustmentRequest {

    @NotBlank(message = "Mã nhân viên không được để trống")
    String employeeId;

    @NotBlank(message = "Tháng không được để trống")
    String month;

    @NotNull(message = "Loại điều chỉnh không được để trống")
    AdjustmentType type;

    @NotNull(message = "Số tiền không được để trống")
    Double amount;

    String description;
}
