package com.example.hrm.dto.request;

import com.example.hrm.enums.PayrollStatus;
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
public class PayrollRequest {

    @NotBlank(message = "Mã nhân viên không được để trống")
    String employeeId;

    @NotBlank(message = "Tháng không được để trống")
    String month;

    Double baseSalary;

    Double allowance;

    Double overtime;

    Double bonus;

    Double penalty;

    Double unpaidLeave;

    Double totalSalary;

    @NotNull(message = "Trạng thái không được để trống")
    PayrollStatus status;
}
