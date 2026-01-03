package com.example.hrm.dto.response;

import com.example.hrm.enums.BasedOn;
import com.example.hrm.enums.PenaltyType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PenaltyRuleResponse {

    String id;

    String code;

    String name;

    Integer minValue;

    Integer maxValue;

    PenaltyType penaltyType;

    BigDecimal penaltyValue;

    BasedOn basedOn;

    Boolean active;

    Integer priority;

    LocalDateTime createdAt;

    LocalDateTime updatedAt;
}
