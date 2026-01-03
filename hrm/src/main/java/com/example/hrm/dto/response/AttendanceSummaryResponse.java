package com.example.hrm.dto.response;

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
