package com.example.hrm.modules.employee.excel.util;

import com.example.hrm.modules.employee.excel.dto.EmployeeExcelImportDto;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class EmployeeUtil{

    // ================== READ CELL ==================

    public List<EmployeeExcelImportDto> buildToDto(Sheet sheet){
        List<EmployeeExcelImportDto> dtos = new ArrayList<>();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            EmployeeExcelImportDto dto = new EmployeeExcelImportDto();
            dto.setCode(getString(row.getCell(0)));
            dto.setFirstName( getString(row.getCell(1)));
            dto.setLastName( getString(row.getCell(2)));
            dto.setDateOfBirth( getLocalDate(row.getCell(3)));
            dto.setGender( getString(row.getCell(4)));
            dto.setEmail( getString(row.getCell(5)));
            dto.setPhone( getString(row.getCell(6)));
            dto.setStatus( getString(row.getCell(7)));
            dto.setJoinDate( getLocalDate(row.getCell(8)));

            dto.setStreet( getString(row.getCell(9)));
            dto.setWard( getString(row.getCell(10)));
            dto.setDistrict( getString(row.getCell(11)));
            dto.setProvince( getString(row.getCell(12)));

            dto.setDepartmentName( getString(row.getCell(13)));
            dto.setPositionName( getString(row.getCell(14)));

            dtos.add(dto);
        }
        return dtos;
    }

    public String getString(Cell cell) {
        if (cell == null) return null;
        cell.setCellType(CellType.STRING);
        String value = cell.getStringCellValue().trim();
        return value.isEmpty() ? null : value;
    }

    public LocalDate getLocalDate(Cell cell) {
        if (cell == null) return null;

        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getLocalDateTimeCellValue().toLocalDate();
        }

        String value = getString(cell);
        if (value == null) return null;

        return LocalDate.parse(value); // yêu cầu yyyy-MM-dd
    }

    public Integer getInteger(Cell cell) {
        if (cell == null) return null;

        if (cell.getCellType() == CellType.NUMERIC) {
            return (int) cell.getNumericCellValue();
        }

        String value = getString(cell);
        if (value == null) return null;

        return Integer.parseInt(value);
    }

    public Double getDouble(Cell cell) {
        if (cell == null) return null;

        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        }

        String value = getString(cell);
        if (value == null) return null;

        return Double.parseDouble(value);
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
