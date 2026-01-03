package com.example.hrm.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PayrollSummaryResponse {

    private BigDecimal grossSalary;

    private BigDecimal totalDeductions;

    private BigDecimal netSalary;
}
