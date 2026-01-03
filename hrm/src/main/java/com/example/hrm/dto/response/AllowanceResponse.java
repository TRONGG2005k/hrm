package com.example.hrm.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AllowanceResponse {
    private String code;
    private String name;
    private BigDecimal amount;
}
