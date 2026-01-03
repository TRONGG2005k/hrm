package com.example.hrm.dto.response;

import lombok.Data;

@Data
public class OvertimeSummaryResponse {
    private Integer weekdayHours;
    private Integer weekendHours;
    private Integer holidayHours;
}
