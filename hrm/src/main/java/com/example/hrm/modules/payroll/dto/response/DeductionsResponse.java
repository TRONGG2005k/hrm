package com.example.hrm.modules.payroll.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

import com.example.hrm.modules.penalty.dto.response.PenaltyResponse;

@Data
public class DeductionsResponse {

    private InsuranceDeductionResponse insurance;

    private BigDecimal personalIncomeTax;

    private List<PenaltyResponse> penalties;

    private BigDecimal advanceSalary;

    private BigDecimal totalDeductions;
}

