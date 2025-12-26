package com.example.hrm.service;

import com.example.hrm.dto.response.AttendanceDetailResponse;
import com.example.hrm.dto.response.AttendanceListResponse;
import com.example.hrm.dto.response.BreakTimeResponse;
import com.example.hrm.enums.AttendanceStatus;
import com.example.hrm.enums.OTType;
import com.example.hrm.exception.AppException;
import com.example.hrm.exception.ErrorCode;
import com.example.hrm.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final AttendanceHelper attendanceHelper;

    public Page<AttendanceListResponse> getAll(int page, int size) {
        return attendanceRepository
                .findByIsDeletedFalse(PageRequest.of(page, size))
                .map(item -> {

                    LocalDateTime checkIn = item.getCheckInTime();
                    LocalDateTime checkOut = item.getCheckOutTime();

                    LocalDateTime shiftStart =
                            attendanceHelper.getShiftStart(item.getEmployee(), checkIn);
                    LocalDateTime shiftEnd =
                            attendanceHelper.getShiftEnd(item.getEmployee(), checkIn);

                    long earlyLeaveMinutes = 0;
                    long lateMinutes = 0;
                    long otMinutes = 0;

                    if (checkIn.isAfter(shiftStart)) {
                        lateMinutes = Duration.between(shiftStart, checkIn).toMinutes();
                    }

                    if (checkOut != null) {
                        if (checkOut.isBefore(shiftEnd)) {
                            earlyLeaveMinutes =
                                    Duration.between(checkOut, shiftEnd).toMinutes();
                        }

                        otMinutes = attendanceHelper.calculateBreakMinutesInOT(
                                item.getBreaks(), shiftEnd, checkOut
                        );
                    }

                    AttendanceStatus status;
                    if (checkOut == null) {
                        status = AttendanceStatus.WORKING;
                    } else if (earlyLeaveMinutes > 0) {
                        status = AttendanceStatus.LEAVE_EARLY;
                    } else if (lateMinutes > 0) {
                        status = AttendanceStatus.LATE;
                    } else if (otMinutes > 0) {
                        status = AttendanceStatus.OVER_TIME;
                    } else {
                        status = AttendanceStatus.ON_TIME;
                    }

                    double otRate = item.getAttendanceOTRates().isEmpty()
                            ? 1.0
                            : item.getAttendanceOTRates().getFirst()
                            .getOtRate().getRate();

                    return AttendanceListResponse.builder()
                            .employeeCode(item.getEmployee().getCode())
                            .checkInTime(checkIn)
                            .checkOutTime(checkOut)
                            .lateMinutes(lateMinutes)
                            .earlyLeaveMinutes(earlyLeaveMinutes)
                            .otMinutes(otMinutes)
                            .otRate(otRate)
                            .workDate(item.getWorkDate())
                            .status(status)
                            .build();
                });
    }

    public AttendanceDetailResponse getDetail(String attendanceId) {

        var attendance = attendanceRepository.findByIdAndIsDeletedFalse(attendanceId)
                .orElseThrow(() -> new AppException(ErrorCode.ATTENDANCE_NOT_FOUND, 404));

        LocalDateTime checkIn = attendance.getCheckInTime();
        LocalDateTime checkOut = attendance.getCheckOutTime();

        var employee = attendance.getEmployee();

        LocalDateTime shiftStart =
                attendanceHelper.getShiftStart(employee, checkIn);
        LocalDateTime shiftEnd =
                attendanceHelper.getShiftEnd(employee, checkIn);

        long lateMinutes = 0;
        long earlyLeaveMinutes = 0;

        if (checkIn != null && checkIn.isAfter(shiftStart)) {
            lateMinutes = Duration.between(shiftStart, checkIn).toMinutes();
        }

        if (checkOut != null && checkOut.isBefore(shiftEnd)) {
            earlyLeaveMinutes = Duration.between(checkOut, shiftEnd).toMinutes();
        }

        long workedMinutes = 0;
        double workedHours = 0;

        if (checkIn != null && checkOut != null) {
            workedMinutes = Duration.between(checkIn, checkOut).toMinutes();
            workedHours = workedMinutes / 60.0;
        }

        long otMinutes = 0;
        if (checkOut != null) {
            otMinutes = attendanceHelper.calculateBreakMinutesInOT(
                    attendance.getBreaks(),
                    shiftEnd,
                    checkOut
            );
        }


        double otHours = otMinutes / 60.0;

        double otRate = attendance.getAttendanceOTRates()
                .isEmpty()
                ? 0
                : attendance.getAttendanceOTRates()
                .getFirst()
                .getOtRate()
                .getRate();

        OTType otType = attendance.getAttendanceOTRates()
                .isEmpty()
                ? null
                : attendance.getAttendanceOTRates()
                .getFirst()
                .getOtRate()
                .getType();

        AttendanceStatus status;

        if (checkOut == null) {
            status = AttendanceStatus.WORKING;
        } else if (earlyLeaveMinutes > 0) {
            status = AttendanceStatus.LEAVE_EARLY;
        } else if (lateMinutes > 0) {
            status = AttendanceStatus.LATE;
        } else if (otMinutes > 0) {
            status = AttendanceStatus.OVER_TIME;
        } else {
            status = AttendanceStatus.ON_TIME;
        }

        var breakResponses = attendance.getBreaks()
                .stream()
                .map(b -> BreakTimeResponse.builder()
                        .id(b.getId())
                        .breakStart(b.getBreakStart())
                        .breakEnd(b.getBreakEnd())
                        .type(b.getType())
                        .build()
                )
                .toList();

        return AttendanceDetailResponse.builder()
                .employeeId(employee.getId())
                .employeeCode(employee.getCode())
                .employeeName(employee.getFirstName() + " " + employee.getLastName())
                .subDepartmentName(employee.getSubDepartment().getName())

                .workDate(attendance.getWorkDate())

                .shiftType(employee.getShiftType())
                .shiftStart(shiftStart)
                .shiftEnd(shiftEnd)

                .checkInTime(checkIn)
                .checkOutTime(checkOut)

                .lateMinutes(lateMinutes)
                .earlyLeaveMinutes(earlyLeaveMinutes)

                .workedMinutes(workedMinutes)
                .workedHours(workedHours)

                .breakTimes(breakResponses)

                .otMinutes(otMinutes)
                .otHours(otHours)
                .otRate(otRate)
                .otType(otType)
                .status(status)
                .build();
    }




}
