package com.example.hrm.service;

import com.example.hrm.entity.Attendance;
import com.example.hrm.entity.BreakTime;
import com.example.hrm.entity.Employee;
import com.example.hrm.enums.ShiftType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceHelper {



    public LocalDateTime getShiftStart(Employee employee, LocalDateTime now) {
        if (employee.getShiftType() == ShiftType.NIGHT) {
            // ca đêm bắt đầu 22:00
            LocalTime nightStart = LocalTime.of(22, 0);

            // nếu check-in sau 00:00 → thuộc ngày hôm trước
            if (now.toLocalTime().isBefore(nightStart)) {
                return now.minusDays(1).toLocalDate().atTime(nightStart);
            }

            return now.toLocalDate().atTime(nightStart);
        }

        // ca sáng
        return now.toLocalDate().atTime(8, 0);
    }

    public LocalDateTime getShiftEnd(Employee employee, LocalDateTime checkIn) {
        if (employee.getShiftType() == ShiftType.NIGHT) {
            return getShiftStart(employee, checkIn).plusHours(8);
        }
        return checkIn.toLocalDate().atTime(17, 0);
    }

    public long calculateBreakMinutesInOT(
            List<BreakTime> breaks,
            LocalDateTime shiftEnd,
            LocalDateTime checkOut
    ) {
        long total = 0;

        for (BreakTime b : breaks) {
            LocalDateTime start = b.getBreakStart().isBefore(shiftEnd)
                    ? shiftEnd
                    : b.getBreakStart();

            LocalDateTime end = b.getBreakEnd().isAfter(checkOut)
                    ? checkOut
                    : b.getBreakEnd();

            if (start.isBefore(end)) {
                total += Duration.between(start, end).toMinutes();
            }
        }

        return total;
    }

    public long calculateWorkedMinutes(Attendance attendance) {
        if (attendance.getCheckInTime() == null || attendance.getCheckOutTime() == null) {
            return 0;
        }
        return Duration.between(
                attendance.getCheckInTime(),
                attendance.getCheckOutTime()
        ).toMinutes();
    }


    public long calculateEarlyLeaveMinutes(Attendance attendance, int graceMinutes) {
        LocalDateTime checkIn = attendance.getCheckInTime();
        LocalDateTime checkOut = attendance.getCheckOutTime();
        LocalDateTime shiftEnd = getShiftEnd(attendance.getEmployee(), checkIn);

        long early = Duration.between(checkOut, shiftEnd).toMinutes();
        return Math.max(0, early - graceMinutes);
    }


    public long calculateLateMinutes(Attendance attendance, int graceMinutes) {
        LocalDateTime checkIn = attendance.getCheckInTime();
        LocalDateTime shiftStart = getShiftStart(attendance.getEmployee(), checkIn);

        long late = Duration.between(shiftStart, checkIn).toMinutes();
        return Math.max(0, late - graceMinutes);
    }

}
