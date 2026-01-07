package com.example.hrm.modules.payroll.dto.response;

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
