package com.example.hrm.dto.request;

import com.example.hrm.enums.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttendanceRequest {

    @NotBlank(message = "Mã nhân viên không được để trống")
    String employeeId;

    @NotNull(message = "Ngày không được để trống")
    LocalDate date;

    LocalDateTime checkInTime;

    LocalDateTime checkoutTime;

    Double lunchHours;

    Double workingHours;

    Integer lateMinutes;

    Integer earlyLeaveMinutes;

    AttendanceStatus status;
}
