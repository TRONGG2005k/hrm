package com.example.hrm.service;


import com.example.hrm.dto.response.AttendanceRealTimeResponse;
import com.example.hrm.entity.*;
import com.example.hrm.enums.AttendanceStatus;
import com.example.hrm.enums.OTType;
import com.example.hrm.enums.ShiftType;
import com.example.hrm.exception.AppException;
import com.example.hrm.exception.ErrorCode;
import com.example.hrm.repository.AttendanceRepository;
import com.example.hrm.repository.BreakTimeRepository;
import com.example.hrm.repository.EmployeeRepository;
import com.example.hrm.repository.OTRateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;
    private final FaceRecognitionService faceRecognitionService;
    private final BreakTimeRepository breakTimeRepository;
    private final OTRateRepository otRateRepository;

    public AttendanceRealTimeResponse scan(MultipartFile request){
        var employeeCode = faceRecognitionService.recognizeFace(request);
        var employee = employeeRepository.findByCodeAndIsDeletedFalse(employeeCode.getCode())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, 404));

        var openAttendanceOpt =
                attendanceRepository.findTopByEmployeeAndCheckOutTimeIsNullOrderByCheckInTimeDesc(employee);

        if (openAttendanceOpt.isEmpty()) {
            return checkIn(employee);
        }

        var now = LocalDateTime.now();
        var attendance = openAttendanceOpt.get();
        var breaks = attendance.getBreaks();

        LocalDate workDate =
                employee.getShiftType() == ShiftType.NIGHT
                        && now.toLocalTime().isBefore(LocalTime.of(22, 0))
                        ? now.minusDays(1).toLocalDate()
                        : now.toLocalDate();

        if (breaks.isEmpty()) {
            LocalTime breakStart = employee.getShiftType() == ShiftType.NIGHT
                    ? LocalTime.of(2, 0)
                    : LocalTime.of(12, 0);

            LocalTime breakEnd = breakStart.plusHours(1);

            BreakTime defaultBreak = new BreakTime();
            defaultBreak.setBreakStart(LocalDateTime.of(workDate, breakStart));
            defaultBreak.setBreakEnd(LocalDateTime.of(workDate, breakEnd));
            defaultBreak.setAttendance(attendance);

            breaks.add(defaultBreak);
            breakTimeRepository.save(defaultBreak);
        }


        // 5️⃣ Kiểm tra xem giờ hiện tại có nằm trong bất kỳ break nào → không tính check-out
        for (BreakTime breakTime : breaks) {
            if (!now.isBefore(breakTime.getBreakStart()) && !now.isAfter(breakTime.getBreakEnd())) {
                return null; // Trong giờ nghỉ
            }
        }

        return checkOut(attendance);
    }

    private AttendanceRealTimeResponse checkIn(Employee employee) {

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime shiftStart = getShiftStart(employee, now);

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


    private AttendanceRealTimeResponse checkOut(Attendance attendance) {

        LocalDateTime checkIn = attendance.getCheckInTime();
        LocalDateTime checkOut = LocalDateTime.now();

        attendance.setCheckOutTime(checkOut);

        LocalDateTime shiftEnd = getShiftEnd(attendance.getEmployee(), checkIn);

        long rawOtMinutes = Math.max(
                0,
                Duration.between(shiftEnd, checkOut).toMinutes()
        );

        long breakOtMinutes = calculateBreakMinutesInOT(
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

        attendanceRepository.save(attendance);

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



    /*
    * helper
    * */

    private LocalDateTime getShiftStart(Employee employee, LocalDateTime now) {
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

    private LocalDateTime getShiftEnd(Employee employee, LocalDateTime checkIn) {
        if (employee.getShiftType() == ShiftType.NIGHT) {
            return getShiftStart(employee, checkIn).plusHours(8);
        }
        return checkIn.toLocalDate().atTime(17, 0);
    }

    private long calculateBreakMinutesInOT(
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


}
