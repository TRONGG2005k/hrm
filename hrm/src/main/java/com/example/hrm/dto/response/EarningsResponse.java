package com.example.hrm.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class EarningsResponse {

    private BigDecimal baseSalary;

    private BigDecimal overtimePay;

    private List<AllowanceResponse> allowances;

    private List<BonusResponse> bonuses;

    private BigDecimal totalEarnings;
}
