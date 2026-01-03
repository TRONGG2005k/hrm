package com.example.hrm.service;

import com.example.hrm.dto.response.AttendanceDetailResponse;
import com.example.hrm.dto.response.AttendanceListResponse;
import com.example.hrm.dto.response.BreakTimeResponse;
import com.example.hrm.entity.Attendance;
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
                .map(this::mapToListResponse);
    }

    public Page<AttendanceListResponse> getAllBySubDepartment(
            int page, int size, String subDepartmentId
    ) {
        return attendanceRepository
                .findBySubDepartmentId(PageRequest.of(page, size), subDepartmentId)
                .map(this::mapToListResponse);
    }

    public AttendanceDetailResponse getDetail(String attendanceId) {

        Attendance attendance = attendanceRepository
                .findByIdAndIsDeletedFalse(attendanceId)
                .orElseThrow(() ->
                        new AppException(ErrorCode.ATTENDANCE_NOT_FOUND, 404)
                );

        LocalDateTime checkIn = attendance.getCheckInTime();
        LocalDateTime checkOut = attendance.getCheckOutTime();
        var employee = attendance.getEmployee();

        LocalDateTime shiftStart =
                attendanceHelper.getShiftStart(employee, checkIn);
        LocalDateTime shiftEnd =
                attendanceHelper.getShiftEnd(employee, checkIn);

        long lateMinutes = attendanceHelper
                .calculateLateMinutes(attendance);

        long earlyLeaveMinutes = 0;
        if (checkOut != null) {
            earlyLeaveMinutes = attendanceHelper
                    .calculateEarlyLeaveMinutes(attendance);
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

        AttendanceStatus status = resolveStatus(
                checkOut,
                lateMinutes,
                earlyLeaveMinutes,
                otMinutes
        );

        double otRate = attendance.getAttendanceOTRates().isEmpty()
                ? 0
                : attendance.getAttendanceOTRates()
                .getFirst()
                .getOtRate()
                .getRate();

        OTType otType = attendance.getAttendanceOTRates().isEmpty()
                ? null
                : attendance.getAttendanceOTRates()
                .getFirst()
                .getOtRate()
                .getType();

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
                .otHours(otMinutes / 60.0)
                .otRate(otRate)
                .otType(otType)

                .status(status)
                .build();
    }

    /* ===================== MAPPER ===================== */

    private AttendanceListResponse mapToListResponse(Attendance attendance) {

        LocalDateTime checkIn = attendance.getCheckInTime();
        LocalDateTime checkOut = attendance.getCheckOutTime();

        LocalDateTime shiftEnd =
                attendanceHelper.getShiftEnd(attendance.getEmployee(), checkIn);

        long lateMinutes =
                attendanceHelper.calculateLateMinutes(attendance);

        long earlyLeaveMinutes = 0;
        long otMinutes = 0;

        if (checkOut != null) {
            earlyLeaveMinutes =
                    attendanceHelper.calculateEarlyLeaveMinutes(attendance);

            otMinutes = attendanceHelper.calculateBreakMinutesInOT(
                    attendance.getBreaks(),
                    shiftEnd,
                    checkOut
            );
        }

        AttendanceStatus status = resolveStatus(
                checkOut,
                lateMinutes,
                earlyLeaveMinutes,
                otMinutes
        );

        double otRate = attendance.getAttendanceOTRates().isEmpty()
                ? 1.0
                : attendance.getAttendanceOTRates()
                .getFirst()
                .getOtRate()
                .getRate();

        return AttendanceListResponse.builder()
                .employeeCode(attendance.getEmployee().getCode())
                .checkInTime(checkIn)
                .checkOutTime(checkOut)
                .lateMinutes(lateMinutes)
                .earlyLeaveMinutes(earlyLeaveMinutes)
                .otMinutes(otMinutes)
                .otRate(otRate)
                .workDate(attendance.getWorkDate())
                .status(status)
                .build();
    }

    /* ===================== STATUS ===================== */

    private AttendanceStatus resolveStatus(
            LocalDateTime checkOut,
            long lateMinutes,
            long earlyLeaveMinutes,
            long otMinutes
    ) {
        if (checkOut == null) {
            return AttendanceStatus.WORKING;
        }
        if (earlyLeaveMinutes > 0) {
            return AttendanceStatus.LEAVE_EARLY;
        }
        if (lateMinutes > 0) {
            return AttendanceStatus.LATE;
        }
        if (otMinutes > 0) {
            return AttendanceStatus.OVER_TIME;
        }
        return AttendanceStatus.ON_TIME;
    }
}

