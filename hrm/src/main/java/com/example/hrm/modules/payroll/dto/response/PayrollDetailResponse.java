package com.example.hrm.modules.payroll.dto.response;

import java.math.BigDecimal;

public record PayrollDetailResponse(
        BigDecimal totalSalary,
        BigDecimal baseSalaryTotal,
        BigDecimal otTotal,
        BigDecimal allowance,
        BigDecimal totalBonus,
        BigDecimal totalPenalty,
        BigDecimal fullAttendanceBonus,
        long workingDays
) {}
