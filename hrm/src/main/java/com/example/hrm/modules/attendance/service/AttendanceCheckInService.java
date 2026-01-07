package com.example.hrm.modules.attendance.service;

import com.example.hrm.modules.attendance.dto.response.AttendanceRealTimeResponse;
import com.example.hrm.modules.attendance.entity.Attendance;
import com.example.hrm.modules.attendance.entity.AttendanceOTRate;
import com.example.hrm.modules.employee.entity.Employee;
import com.example.hrm.modules.attendance.entity.OTRate;
import com.example.hrm.shared.enums.AttendanceStatus;
import com.example.hrm.shared.enums.OTType;
import com.example.hrm.shared.enums.ShiftType;
import com.example.hrm.shared.exception.AppException;
import com.example.hrm.shared.exception.ErrorCode;
import com.example.hrm.modules.attendance.repository.AttendanceRepository;
import com.example.hrm.modules.attendance.repository.OTRateRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceCheckInService {

    private final AttendanceRepository attendanceRepository;
    private final OTRateRepository otRateRepository;
    private final AttendanceHelper attendancePolicy;

    public AttendanceRealTimeResponse checkIn(Employee employee) {

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime shiftStart = attendancePolicy.getShiftStart(employee, now);

        AttendanceStatus status =
                now.isAfter(shiftStart)
                        ? AttendanceStatus.LATE
                        : AttendanceStatus.ON_TIME;

        LocalDate workDate;
        if (employee.getShiftType() == ShiftType.NIGHT
                && now.toLocalTime().isBefore(LocalTime.of(22, 0))) {
            workDate = now.minusDays(1).toLocalDate();
        } else {
            workDate = now.toLocalDate();
        }

        Attendance attendance = Attendance.builder()
                .workDate(LocalDate.now())
                .employee(employee)
                .checkInTime(now)
                .status(status)
                .isDeleted(false)
                .breaks(new ArrayList<>())
                .attendanceOTRates(new ArrayList<>())
                .build();

        DayOfWeek dayOfWeek = workDate.getDayOfWeek();
        boolean isSunday = dayOfWeek == DayOfWeek.SUNDAY;

        OTRate otRate;

        if (otRateRepository.existsByDateAndTypeAndIsDeletedFalse(workDate, OTType.HOLIDAY)) {
            otRate = otRateRepository
                    .findByDateAndTypeAndIsDeletedFalse(workDate, OTType.HOLIDAY)
                    .orElseThrow(() -> new AppException(ErrorCode.OT_RATE_NOT_FOUND, 404));

        } else if (isSunday) {
            otRate = otRateRepository
                    .findByTypeAndIsDeletedFalse(OTType.SUNDAY)
                    .orElseThrow(() -> new AppException(ErrorCode.OT_RATE_NOT_FOUND, 404));

        } else {
            otRate = otRateRepository
                    .findByTypeAndIsDeletedFalse(OTType.NORMAL)
                    .orElseThrow(() -> new AppException(ErrorCode.OT_RATE_NOT_FOUND, 404));
        }

        AttendanceOTRate attendanceOTRate = new AttendanceOTRate();
        attendanceOTRate.setAttendance(attendance); // 🔥 BẮT BUỘC
        attendanceOTRate.setOtRate(otRate);
        attendanceOTRate.setOtHours(0.0);
        attendanceOTRate.setIsDeleted(false);

        attendance.getAttendanceOTRates().add(attendanceOTRate);

        attendanceRepository.save(attendance);

        return AttendanceRealTimeResponse.builder()
                .employeeCode(employee.getCode())
                .employeeName(employee.getFirstName() + " " + employee.getLastName())
                .time(now)
                .status(status)
                .message(
                        status == AttendanceStatus.LATE
                                ? "Bạn đã đi trễ"
                                : "Check-in thành công"
                )
                .build();
    }

}
