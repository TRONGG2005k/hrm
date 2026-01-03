package com.example.hrm.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class DeductionsResponse {

    private InsuranceDeductionResponse insurance;

    private BigDecimal personalIncomeTax;

    private List<PenaltyResponse> penalties;

    private BigDecimal advanceSalary;

    private BigDecimal totalDeductions;
}

