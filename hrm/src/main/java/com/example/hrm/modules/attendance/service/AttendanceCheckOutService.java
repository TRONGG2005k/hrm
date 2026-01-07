package com.example.hrm.modules.attendance.service;

import com.example.hrm.modules.attendance.dto.response.AttendanceRealTimeResponse;
import com.example.hrm.modules.attendance.entity.Attendance;
import com.example.hrm.modules.attendance.entity.AttendanceOTRate;
import com.example.hrm.shared.enums.AttendanceStatus;
import com.example.hrm.shared.exception.AppException;
import com.example.hrm.shared.exception.ErrorCode;
import com.example.hrm.modules.attendance.repository.AttendanceRepository;
import com.example.hrm.modules.penalty.service.AttendancePenaltyService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceCheckOutService {

    private final AttendanceRepository attendanceRepository;
    private final AttendanceHelper attendancePolicy;
    private final AttendancePenaltyService attendancePenaltyService;
    private final AttendanceHelper attendanceHelper;
    public AttendanceRealTimeResponse checkOut(Attendance attendance) {

        LocalDateTime checkIn = attendance.getCheckInTime();
        LocalDateTime checkOut = LocalDateTime.now();

        attendance.setCheckOutTime(checkOut);

        LocalDateTime shiftEnd = attendancePolicy.getShiftEnd(attendance.getEmployee(), checkIn);

        long rawOtMinutes = Math.max(
                0,
                Duration.between(shiftEnd, checkOut).toMinutes()
        );

        long breakOtMinutes = attendancePolicy.calculateBreakMinutesInOT(
                attendance.getBreaks(),
                shiftEnd,
                checkOut
        );

        long actualOtMinutes = Math.max(0, rawOtMinutes - breakOtMinutes);

        double otHours = actualOtMinutes >= 120
                ? actualOtMinutes / 60.0
                : 0.0;

        AttendanceOTRate otRate = attendance.getAttendanceOTRates()
                .stream()
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.OT_RATE_NOT_FOUND, 500));

        otRate.setOtHours(otHours);

        long workedMinutes = Duration.between(checkIn, checkOut).toMinutes();

        if (workedMinutes < 8 * 60) {
            attendance.setStatus(AttendanceStatus.LEAVE_EARLY);
        } else if (otHours > 0) {
            attendance.setStatus(AttendanceStatus.OVER_TIME);
        } else {
            attendance.setStatus(AttendanceStatus.ON_TIME);
        }

        attendanceHelper.analyzeAttendance(attendance);
        attendanceRepository.save(attendance);
        attendancePenaltyService.calculateAndSave(attendance);

        return AttendanceRealTimeResponse.builder()
                .employeeCode(attendance.getEmployee().getCode())
                .employeeName(
                        attendance.getEmployee().getFirstName()
                                + " " + attendance.getEmployee().getLastName()
                )
                .time(checkOut)
                .status(attendance.getStatus())
                .message("Check-out thành công")
                .build();
    }
}
