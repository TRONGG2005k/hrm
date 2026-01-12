package com.example.hrm.modules.payroll.dto.response;

import com.example.hrm.modules.contract.dto.response.SalaryAdjustmentResponse;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class EarningsResponse {

    private BigDecimal baseSalary;

    private BigDecimal overtimePay;

    private BigDecimal allowances;

    private List<SalaryAdjustmentResponse> bonuses;

    private BigDecimal totalEarnings;
}
