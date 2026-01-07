package com.example.hrm.modules.attendance.dto.response;

import com.example.hrm.modules.payroll.dto.response.OvertimeSummaryResponse;

import lombok.Data;

@Data
public class AttendanceSummaryResponse {

    private Integer workingDays;
    private Integer paidLeaveDays;
    private Integer unpaidLeaveDays;

    private Integer lateMinutes;
    private Integer earlyLeaveMinutes;

    private OvertimeSummaryResponse overtime;
}
