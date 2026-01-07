package com.example.hrm.modules.penalty.dto.request;

import com.example.hrm.shared.enums.BasedOn;
import com.example.hrm.shared.enums.PenaltyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PenaltyRuleRequest {

    @NotBlank(message = "Code không được để trống")
    String code;

    @NotBlank(message = "Tên quy tắc không được để trống")
    String name;

    @NotNull(message = "Giá trị tối thiểu không được để trống")
    Integer minValue;

    Integer maxValue;

    @NotNull(message = "Loại phạt không được để trống")
    PenaltyType penaltyType;

    @NotNull(message = "Giá trị phạt không được để trống")
    BigDecimal penaltyValue;

    @NotNull(message = "Dựa trên không được để trống")
    BasedOn basedOn;

    Boolean active;

    Integer priority;
}
