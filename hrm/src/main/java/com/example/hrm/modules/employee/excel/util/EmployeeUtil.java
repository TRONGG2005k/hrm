package com.example.hrm.modules.employee.excel.util;

import com.example.hrm.modules.employee.excel.dto.EmployeeExcelExportDto;
import com.example.hrm.modules.employee.excel.dto.EmployeeExcelImportDto;
import com.example.hrm.modules.employee.excel.mapper.EnumMapper;
import com.example.hrm.shared.enums.ShiftType;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class EmployeeUtil {

    private final DataFormatter formatter = new DataFormatter();
    private final EnumMapper enumMapper;

    public EmployeeUtil(EnumMapper enumMapper) {
        this.enumMapper = enumMapper;
    }

    // ================== READ SHEET ==================

    public List<EmployeeExcelImportDto> buildToDto(Sheet sheet) {
        List<EmployeeExcelImportDto> dtos = new ArrayList<>();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) { // dòng 0 là header
            Row row = sheet.getRow(i);
            if (row == null) continue;

            EmployeeExcelImportDto dto = new EmployeeExcelImportDto();
            dto.setCode(getString(row.getCell(0)));
            dto.setFirstName(getString(row.getCell(1)));
            dto.setLastName(getString(row.getCell(2)));
            dto.setDateOfBirth(getLocalDate(row.getCell(3)));
            dto.setGender(getString(row.getCell(4)));
            dto.setEmail(getString(row.getCell(5)));
            dto.setPhone(getString(row.getCell(6)));
            dto.setStatus(getString(row.getCell(7)));
            dto.setJoinDate(getLocalDate(row.getCell(8)));
            dto.setShiftType(getShiftType(row.getCell(9)));

            dto.setStreet(getString(row.getCell(10)));
            dto.setWard(getString(row.getCell(11)));
            dto.setDistrict(getString(row.getCell(12)));
            dto.setProvince(getString(row.getCell(13)));

            dto.setDepartmentName(getString(row.getCell(14)));
            dto.setPositionName(getString(row.getCell(15)));

            dtos.add(dto);
        }
        return dtos;
    }

    public void buildRow(Row row, EmployeeExcelExportDto dto){
        row.createCell(0).setCellValue(dto.getCode());
        row.createCell(1).setCellValue(dto.getFirstName());
        row.createCell(2).setCellValue(dto.getLastName());
        row.createCell(3).setCellValue(
                dto.getDateOfBirth() != null ? dto.getDateOfBirth().toString() : ""
        );
        row.createCell(4).setCellValue(dto.getGender());
        row.createCell(5).setCellValue(dto.getEmail());
        row.createCell(6).setCellValue(dto.getPhone());
        row.createCell(7).setCellValue(dto.getStatus());
        row.createCell(8).setCellValue(dto.getJoinDate().toString());
        row.createCell(9).setCellValue(dto.getShiftType().name());
        row.createCell(10).setCellValue(dto.getStreet());
        row.createCell(11).setCellValue(dto.getWard());
        row.createCell(12).setCellValue(dto.getDistrict());
        row.createCell(13).setCellValue(dto.getProvince());
        row.createCell(14).setCellValue(dto.getDepartmentName());
        row.createCell(15).setCellValue(dto.getPositionName());

    }
    // ================== CELL READ ==================

    public String getString(Cell cell) {
        if (cell == null) return null;
        String value = formatter.formatCellValue(cell).trim();
        return value.isEmpty() ? null : value;
    }

    public LocalDate getLocalDate(Cell cell) {
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

    public ShiftType getShiftType(Cell cell) {
        String value = getString(cell);
        if (value == null) return null;
        return enumMapper.mapShiftType(value);
    }

    public Integer getInteger(Cell cell) {
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

    public Double getDouble(Cell cell) {
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

    public boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public boolean isValidEmail(String email) {
        if (isBlank(email)) return false;
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    public boolean isValidPhone(String phone) {
        if (isBlank(phone)) return false;
        return phone.matches("^0[0-9]{9}$"); // Việt Nam: 10 số bắt đầu bằng 0
    }

    public boolean isValidEnum(Class<? extends Enum<?>> enumClass, String value) {
        if (isBlank(value)) return false;
        try {
            Enum.valueOf((Class) enumClass, value.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isPastDate(LocalDate date) {
        return date != null && date.isBefore(LocalDate.now());
    }

    public boolean isFutureDate(LocalDate date) {
        return date != null && date.isAfter(LocalDate.now());
    }

    public boolean isValidDateFormat(String value) {
        if (isBlank(value)) return false;
        try {
            LocalDate.parse(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isPositiveNumber(Number number) {
        return number != null && number.doubleValue() > 0;
    }

    public boolean isNonNegativeNumber(Number number) {
        return number != null && number.doubleValue() >= 0;
    }

    public boolean isLengthBetween(String value, int min, int max) {
        if (value == null) return false;
        int len = value.length();
        return len >= min && len <= max;
    }
}
