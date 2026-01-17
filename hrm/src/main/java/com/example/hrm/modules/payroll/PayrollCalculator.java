package com.example.hrm.modules.payroll;

import com.example.hrm.modules.attendance.entity.Attendance;
import com.example.hrm.modules.attendance.entity.AttendanceOTRate;
import com.example.hrm.modules.contract.dto.response.AllowanceSummary;
import com.example.hrm.modules.contract.entity.AllowanceRule;
import com.example.hrm.modules.contract.entity.SalaryAdjustment;
import com.example.hrm.modules.payroll.dto.response.PayrollCycleResponse;
import com.example.hrm.modules.payroll.dto.response.PayrollDetailResponse;
import com.example.hrm.modules.penalty.PenaltyAmountCalculator;
import com.example.hrm.modules.penalty.dto.response.AttendancePenaltyResult;
import com.example.hrm.shared.enums.AdjustmentType;
import com.example.hrm.shared.enums.AllowanceCalculationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PayrollCalculator {
    private final PenaltyAmountCalculator penaltyAmountCalculator;

    public PayrollDetailResponse calculatePayrollDetail(
            BigDecimal baseSalary,
            List<Attendance> attendanceList,
            List<SalaryAdjustment> adjustments,
            List<AllowanceRule> allowances,
            PayrollCycleResponse cycle) {

        BigDecimal totalSalary = BigDecimal.ZERO;
        BigDecimal baseSalaryTotal = BigDecimal.ZERO;
        BigDecimal otTotal = BigDecimal.ZERO;
        BigDecimal totalBonus = BigDecimal.ZERO;
        BigDecimal totalPenalty = BigDecimal.ZERO;
        BigDecimal fullAttendanceBonus = BigDecimal.ZERO;

        long actualWorkingDays = 0;
        long otHours = 0;
        BigDecimal salaryPerDay = calculateSalaryPerDay(baseSalary, cycle);

        for (Attendance att : attendanceList) {
            AttendancePenaltyResult penalty = penaltyAmountCalculator.applyAttendancePenaltyRule(att);

            if (!penalty.isVoidBaseSalary()) {
                BigDecimal dailySalary = calculateNetDailySalary(salaryPerDay, penalty);
                totalSalary = totalSalary.add(dailySalary);
                baseSalaryTotal = baseSalaryTotal.add(dailySalary);
                actualWorkingDays++;
            }

            if (!penalty.isVoidOvertime()) {
                for (var otRate : att.getAttendanceOTRates()) {
                    otHours += otRate.getOtHours();
                    BigDecimal otMoney = calculateOt(otRate, salaryPerDay);
                    totalSalary = totalSalary.add(otMoney);
                    otTotal = otTotal.add(otMoney);
                }
            }
        }
        var mapAllowance = calculateAllowanceTotalsByType(allowances, cycle.getWorkingDays(), actualWorkingDays, otHours);
        var totalAllowance = mapAllowance.values().stream()
                .map(AllowanceSummary::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        totalSalary = totalSalary.add(totalAllowance);


        for (SalaryAdjustment adj : adjustments) {
            if (adj.getType() == AdjustmentType.BONUS) {
                totalSalary = totalSalary.add(adj.getAmount());
                totalBonus = totalBonus.add(adj.getAmount());
            } else if (adj.getType() == AdjustmentType.PENALTY) {
                totalSalary = totalSalary.subtract(adj.getAmount());
                totalPenalty = totalPenalty.add(adj.getAmount());
            }
        }

        if (actualWorkingDays == cycle.getWorkingDays()) {
            totalSalary = totalSalary.add(BigDecimal.valueOf(500_000));
            fullAttendanceBonus = BigDecimal.valueOf(500_000);
        }

        return new PayrollDetailResponse(
                totalSalary,
                baseSalaryTotal,
                otTotal,
                mapAllowance,
                totalAllowance,
                totalBonus,
                totalPenalty,
                fullAttendanceBonus,
                actualWorkingDays
        );
    }

    public Map<AllowanceCalculationType, AllowanceSummary> calculateAllowanceTotalsByType(
            List<AllowanceRule> allowances,
            int standardWorkingDays,
            long actualWorkingDays,
            long otHours
    ) {
        Map<AllowanceCalculationType, AllowanceSummary> totals = new EnumMap<>(AllowanceCalculationType.class);

        for (AllowanceRule allowance : allowances) {
            BigDecimal amount = switch (allowance.getCalculationType()) {
                case FIXED, PER_DAY -> allowance.getAmount();
                case PER_WORKING_DAY -> allowance.getAmount()
                        .divide(BigDecimal.valueOf(standardWorkingDays), 2, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(actualWorkingDays));
                case PER_OT_HOUR -> allowance.getAmount()
                        .multiply(BigDecimal.valueOf(otHours));
            };

            totals.merge(
                    allowance.getCalculationType(),
                    new AllowanceSummary(allowance.getAllowance().getName(), amount),
                    (oldVal, newVal) -> new AllowanceSummary(
                            oldVal.name(),
                            oldVal.amount().add(newVal.amount())
                    )
            );
        }

        return totals;
    }




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
