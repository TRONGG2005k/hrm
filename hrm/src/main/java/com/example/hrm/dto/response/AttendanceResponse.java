package com.example.hrm.dto.response;

import com.example.hrm.enums.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttendanceResponse {

    String id;

    String employeeId;

    LocalDate date;

    LocalDateTime checkInTime;

    LocalDateTime checkoutTime;

    Double lunchHours;

    Double workingHours;

    Integer lateMinutes;

    Integer earlyLeaveMinutes;

    AttendanceStatus status;

    LocalDateTime createdAt;

    LocalDateTime updatedAt;

    Boolean isDeleted;

    LocalDateTime deletedAt;
}
