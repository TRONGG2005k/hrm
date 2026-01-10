package com.example.hrm.modules.payroll;

import com.example.hrm.modules.attendance.entity.AttendanceOTRate;
import com.example.hrm.modules.payroll.dto.response.PayrollCycleResponse;
import com.example.hrm.modules.penalty.dto.response.AttendancePenaltyResult;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class PayrollCalculator {
    public BigDecimal calculateSalaryPerDay(BigDecimal monthlySalary, PayrollCycleResponse cycle) {
        int workingDays = cycle.getWorkingDays();
        if (workingDays == 0) {
            throw new IllegalArgumentException("Working days in payroll cycle cannot be zero");
        }
        return monthlySalary.divide(
                BigDecimal.valueOf(workingDays),
                2,
                RoundingMode.HALF_UP);
    }

    public BigDecimal calculateSalaryPerHour(BigDecimal monthlySalary, PayrollCycleResponse cycle) {
        BigDecimal salaryPerDay = calculateSalaryPerDay(monthlySalary, cycle);
        return salaryPerDay.divide(
                BigDecimal.valueOf(8),
                2,
                RoundingMode.HALF_UP);
    }

    public BigDecimal calculateNetDailySalary(
            BigDecimal salaryPerDay,
            AttendancePenaltyResult penaltyResult) {
        BigDecimal penalty = BigDecimal
                .valueOf(penaltyResult.getPenaltyAmount())
                .min(salaryPerDay);

        return salaryPerDay.subtract(penalty);
    }

    public BigDecimal calculateOt(
            AttendanceOTRate otRate,
            BigDecimal salaryPerDay
    ) {
        BigDecimal salaryPerHour = salaryPerDay.divide(
                BigDecimal.valueOf(8),
                6,
                RoundingMode.HALF_UP
        );

        BigDecimal rate =
                BigDecimal.valueOf(otRate.getOtRate().getRate());

        BigDecimal otHours =
                BigDecimal.valueOf(otRate.getOtHours());

        return salaryPerHour
                .multiply(rate)
                .multiply(otHours)
                .setScale(0, RoundingMode.HALF_UP); // tiền → làm tròn
    }
}
