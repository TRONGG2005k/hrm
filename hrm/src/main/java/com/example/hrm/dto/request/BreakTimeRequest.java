package com.example.hrm.dto.request;

import com.example.hrm.enums.BreakType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BreakTimeRequest {
    private String attendanceId;
    private LocalDateTime breakStart;
    private LocalDateTime breakEnd;
    private BreakType type;
}
