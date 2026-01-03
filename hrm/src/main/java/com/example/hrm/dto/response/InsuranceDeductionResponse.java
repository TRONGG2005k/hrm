package com.example.hrm.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InsuranceDeductionResponse {
    private BigDecimal socialInsurance;
    private BigDecimal healthInsurance;
    private BigDecimal unemploymentInsurance;
}

