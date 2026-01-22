package com.example.hrm.modules.attendance.service;

import com.example.hrm.modules.attendance.dto.request.AttendanceImportDto;
import com.example.hrm.modules.attendance.dto.response.AttendanceDetailResponse;
import com.example.hrm.modules.attendance.dto.response.AttendanceImportResponse;
import com.example.hrm.modules.attendance.dto.response.AttendanceListResponse;
import com.example.hrm.modules.attendance.dto.response.BreakTimeResponse;
import com.example.hrm.modules.attendance.entity.Attendance;
import com.example.hrm.modules.employee.repository.EmployeeRepository;
import com.example.hrm.modules.payroll.dto.response.ImportErrorDto;
import com.example.hrm.shared.enums.AttendanceEvaluation;
import com.example.hrm.shared.enums.AttendanceStatus;
import com.example.hrm.shared.enums.OTType;
import com.example.hrm.shared.exception.AppException;
import com.example.hrm.shared.exception.ErrorCode;
import com.example.hrm.modules.attendance.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceService {


    private final AttendanceRepository attendanceRepository;
    private final AttendanceHelper attendanceHelper;
    private final EmployeeRepository employeeRepository;

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

        var status = resolveStatus(
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

        Object status = resolveStatus(
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

    private Object resolveStatus(
            LocalDateTime checkOut,
            long lateMinutes,
            long earlyLeaveMinutes,
            long otMinutes
    ) {
        if (checkOut == null) {
            return AttendanceStatus.WORKING;
        }
        if (earlyLeaveMinutes > 0) {
            return AttendanceEvaluation.LEAVE_EARLY;
        }
        if (lateMinutes > 0) {
            return AttendanceEvaluation.LATE;
        }
        if (otMinutes > 0) {
            return AttendanceEvaluation.OVER_TIME;
        }
        return AttendanceEvaluation.ON_TIME;
    }

    /**
     * Export attendance data to Excel file
     * @param page page number (0-based)
     * @param size page size
     * @return byte array of Excel file
     */
    public byte[] exportAttendanceToExcel(int page, int size) throws IOException {
        Page<Attendance> attendancePage = attendanceRepository.findByIsDeletedFalse(PageRequest.of(page, size));

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Attendance Data");

        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Employee Code", "Check In Time", "Check Out Time", "Work Date", "Status", "Evaluation"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // Fill data rows
        int rowNum = 1;
        for (Attendance attendance : attendancePage.getContent()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(attendance.getEmployee().getCode());
            row.createCell(1).setCellValue(attendance.getCheckInTime() != null ? attendance.getCheckInTime().toString() : "");
            row.createCell(2).setCellValue(attendance.getCheckOutTime() != null ? attendance.getCheckOutTime().toString() : "");
            row.createCell(3).setCellValue(attendance.getWorkDate() != null ? attendance.getWorkDate().toString() : "");
            row.createCell(4).setCellValue(attendance.getStatus() != null ? attendance.getStatus().toString() : "");
            row.createCell(5).setCellValue(attendance.getEvaluation() != null ? attendance.getEvaluation().toString() : "");
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Write to byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }

    /**
     * Import attendance data from Excel file
     * @param file the Excel file to import
     * @return import response with results and errors
     */
    @Transactional
    public AttendanceImportResponse importAttendanceFromExcel(MultipartFile file) throws IOException {
        List<AttendanceImportDto> importData = parseExcelFile(file);
        List<ImportErrorDto> errors = new ArrayList<>();
        List<String> importedIds = new ArrayList<>();
        int successCount = 0;

        for (AttendanceImportDto dto : importData) {
            try {
                validateImportDto(dto);
                Attendance attendance = createAttendanceFromImportDto(dto);
                attendanceRepository.save(attendance);
                importedIds.add(attendance.getId());
                successCount++;
            } catch (Exception e) {
                errors.add(ImportErrorDto.builder()
                        .rowNumber(dto.getRowNumber())
                        .message(e.getMessage())
                        .build());
            }
        }

        return AttendanceImportResponse.builder()
                .totalRows(importData.size())
                .successCount(successCount)
                .errorCount(errors.size())
                .errors(errors)
                .importedAttendanceIds(importedIds)
                .build();
    }

    private List<AttendanceImportDto> parseExcelFile(MultipartFile file) throws IOException {
        List<AttendanceImportDto> data = new ArrayList<>();

        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        // Skip header row
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            AttendanceImportDto dto = AttendanceImportDto.builder()
                    .rowNumber(i + 1)
                    .employeeCode(getCellValueAsString(row.getCell(0)))
                    .checkInTime(getCellValueAsString(row.getCell(1)))
                    .checkOutTime(getCellValueAsString(row.getCell(2)))
                    .workDate(getCellValueAsString(row.getCell(3)))
                    .status(getCellValueAsString(row.getCell(4)))
                    .evaluation(getCellValueAsString(row.getCell(5)))
                    .build();

            data.add(dto);
        }

        workbook.close();
        return data;
    }

    private void validateImportDto(AttendanceImportDto dto) {
        if (dto.getEmployeeCode() == null || dto.getEmployeeCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Employee code is required");
        }

        // Check if employee exists
        if (employeeRepository.findByCodeAndIsDeletedFalse(dto.getEmployeeCode()).isEmpty()) {
            throw new IllegalArgumentException("Employee with code " + dto.getEmployeeCode() + " not found");
        }

        // Validate date formats
        if (dto.getCheckInTime() != null && !dto.getCheckInTime().trim().isEmpty()) {
            try {
                LocalDateTime.parse(dto.getCheckInTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid check-in time format. Use YYYY-MM-DD HH:mm:ss");
            }
        }

        if (dto.getCheckOutTime() != null && !dto.getCheckOutTime().trim().isEmpty()) {
            try {
                LocalDateTime.parse(dto.getCheckOutTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid check-out time format. Use YYYY-MM-DD HH:mm:ss");
            }
        }

        if (dto.getWorkDate() != null && !dto.getWorkDate().trim().isEmpty()) {
            try {
                LocalDate.parse(dto.getWorkDate());
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid work date format. Use YYYY-MM-DD");
            }
        }

        // Validate enums
        if (dto.getStatus() != null && !dto.getStatus().trim().isEmpty()) {
            try {
                AttendanceStatus.valueOf(dto.getStatus().toUpperCase());
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid status. Valid values: " + java.util.Arrays.toString(AttendanceStatus.values()));
            }
        }

        if (dto.getEvaluation() != null && !dto.getEvaluation().trim().isEmpty()) {
            try {
                AttendanceEvaluation.valueOf(dto.getEvaluation().toUpperCase());
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid evaluation. Valid values: " + java.util.Arrays.toString(AttendanceEvaluation.values()));
            }
        }
    }

    private Attendance createAttendanceFromImportDto(AttendanceImportDto dto) {
        var employee = employeeRepository.findByCodeAndIsDeletedFalse(dto.getEmployeeCode())
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        Attendance attendance = Attendance.builder()
                .employee(employee)
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .build();

        // Set optional fields
        if (dto.getCheckInTime() != null && !dto.getCheckInTime().trim().isEmpty()) {
            attendance.setCheckInTime(LocalDateTime.parse(dto.getCheckInTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        if (dto.getCheckOutTime() != null && !dto.getCheckOutTime().trim().isEmpty()) {
            attendance.setCheckOutTime(LocalDateTime.parse(dto.getCheckOutTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        if (dto.getWorkDate() != null && !dto.getWorkDate().trim().isEmpty()) {
            attendance.setWorkDate(LocalDate.parse(dto.getWorkDate()));
        }

        if (dto.getStatus() != null && !dto.getStatus().trim().isEmpty()) {
            attendance.setStatus(AttendanceStatus.valueOf(dto.getStatus().toUpperCase()));
        }

        if (dto.getEvaluation() != null && !dto.getEvaluation().trim().isEmpty()) {
            attendance.setEvaluation(AttendanceEvaluation.valueOf(dto.getEvaluation().toUpperCase()));
        }

        return attendance;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> null;
        };
    }
}
