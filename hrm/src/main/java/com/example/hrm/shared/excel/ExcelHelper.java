package com.example.hrm.shared.excel;

import com.example.hrm.shared.enums.*;
import com.example.hrm.shared.mapper.EnumMapper;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@AllArgsConstructor
public class ExcelHelper {

    private static EnumMapper enumMapper;

    private static final DataFormatter formatter = new DataFormatter();

    public static String getString(Cell cell) {
        if (cell == null) return null;
        String value = formatter.formatCellValue(cell).trim();
        return value.isEmpty() ? null : value;
    }

    public static LocalDate getLocalDate(Cell cell) {
        if (cell == null) return null;

        // Nếu là kiểu Date trong Excel
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getLocalDateTimeCellValue().toLocalDate();
        }

        // Nếu là text
        String value = getString(cell);
        if (value == null) return null;

        // Thử các format phổ biến
        try {
            return LocalDate.parse(value); // yyyy-MM-dd
        } catch (Exception e) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                return LocalDate.parse(value, formatter);
            } catch (Exception ex) {
                return null;
            }
        }
    }

    public static ShiftType getShiftType(Cell cell) {
        String value = getString(cell);
        if (value == null) return null;
        return enumMapper.mapShiftType(value);
    }

    public static Gender getGender(Cell cell) {
        String value = getString(cell);
        if (value == null) return null;
        return enumMapper.mapGender(value);
    }

    public static EmployeeStatus getEmployeeStatus(Cell cell) {
        String value = getString(cell);
        if (value == null) return null;
        return enumMapper.mapEmployeeStatus(value);
    }

    public static AdjustmentType getAdjustmentType(Cell cell) {
        String value = getString(cell);
        if (value == null) return null;
        return enumMapper.mapAdjustmentType(value);
    }

    public static AllowanceCalculationType getAllowanceCalculationType(Cell cell) {
        String value = getString(cell);
        if (value == null) return null;
        return enumMapper.mapAllowanceCalculationType(value);
    }

    public static AttendanceEvaluation getAttendanceEvaluation(Cell cell) {
        String value = getString(cell);
        if (value == null) return null;
        return enumMapper.mapAttendanceEvaluation(value);
    }

    public static AttendanceStatus getAttendanceStatus(Cell cell) {
        String value = getString(cell);
        if (value == null) return null;
        return enumMapper.mapAttendanceStatus(value);
    }

    public static BasedOn getBasedOn(Cell cell) {
        String value = getString(cell);
        if (value == null) return null;
        return enumMapper.mapBasedOn(value);
    }

    public static BreakType getBreakType(Cell cell) {
        String value = getString(cell);
        if (value == null) return null;
        return enumMapper.mapBreakType(value);
    }

    public static ContactType getContactType(Cell cell) {
        String value = getString(cell);
        if (value == null) return null;
        return enumMapper.mapContactType(value);
    }

    public static ContractStatus getContractStatus(Cell cell) {
        String value = getString(cell);
        if (value == null) return null;
        return enumMapper.mapContractStatus(value);
    }

    public static ContractType getContractType(Cell cell) {
        String value = getString(cell);
        if (value == null) return null;
        return enumMapper.mapContractType(value);
    }

    public static LeaveStatus getLeaveStatus(Cell cell) {
        String value = getString(cell);
        if (value == null) return null;
        return enumMapper.mapLeaveStatus(value);
    }

    public static LeaveType getLeaveType(Cell cell) {
        String value = getString(cell);
        if (value == null) return null;
        return enumMapper.mapLeaveType(value);
    }

    public static OTType getOTType(Cell cell) {
        String value = getString(cell);
        if (value == null) return null;
        return enumMapper.mapOTType(value);
    }

    public static PayrollApprovalStatus getPayrollApprovalStatus(Cell cell) {
        String value = getString(cell);
        if (value == null) return null;
        return enumMapper.mapPayrollApprovalStatus(value);
    }

    public static PayrollStatus getPayrollStatus(Cell cell) {
        String value = getString(cell);
        if (value == null) return null;
        return enumMapper.mapPayrollStatus(value);
    }

    public static PenaltyType getPenaltyType(Cell cell) {
        String value = getString(cell);
        if (value == null) return null;
        return enumMapper.mapPenaltyType(value);
    }

    public static RefType getRefType(Cell cell) {
        String value = getString(cell);
        if (value == null) return null;
        return enumMapper.mapRefType(value);
    }

    public static SalaryContractStatus getSalaryContractStatus(Cell cell) {
        String value = getString(cell);
        if (value == null) return null;
        return enumMapper.mapSalaryContractStatus(value);
    }

    public static TokenType getTokenType(Cell cell) {
        String value = getString(cell);
        if (value == null) return null;
        return enumMapper.mapTokenType(value);
    }

    public static UserStatus getUserStatus(Cell cell) {
        String value = getString(cell);
        if (value == null) return null;
        return enumMapper.mapUserStatus(value);
    }

    public static Integer getInteger(Cell cell) {
        if (cell == null) return null;

        if (cell.getCellType() == CellType.NUMERIC) {
            return (int) cell.getNumericCellValue();
        }

        String value = getString(cell);
        if (value == null) return null;

        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return null;
        }
    }

    public static Double getDouble(Cell cell) {
        if (cell == null) return null;

        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        }

        String value = getString(cell);
        if (value == null) return null;

        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            return null;
        }
    }

    // ================== VALIDATE ==================

    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        if (isBlank(email)) return false;
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    public static boolean isValidPhone(String phone) {
        if (isBlank(phone)) return false;
        return phone.matches("^0[0-9]{9}$"); // Việt Nam: 10 số bắt đầu bằng 0
    }

    public static boolean isValidEnum(Class<? extends Enum<?>> enumClass, String value) {
        if (isBlank(value)) return false;
        try {
            Enum.valueOf((Class) enumClass, value.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean isPastDate(LocalDate date) {
        return date != null && date.isBefore(LocalDate.now());
    }

    public static boolean isFutureDate(LocalDate date) {
        return date != null && date.isAfter(LocalDate.now());
    }

    public static boolean isValidDateFormat(String value) {
        if (isBlank(value)) return false;
        try {
            LocalDate.parse(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isPositiveNumber(Number number) {
        return number != null && number.doubleValue() > 0;
    }

    public static boolean isNonNegativeNumber(Number number) {
        return number != null && number.doubleValue() >= 0;
    }

    public static boolean isLengthBetween(String value, int min, int max) {
        if (value == null) return false;
        int len = value.length();
        return len >= min && len <= max;
    }
}
