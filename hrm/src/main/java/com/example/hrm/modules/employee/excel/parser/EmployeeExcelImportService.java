package com.example.hrm.modules.employee.excel.parser;


import com.example.hrm.modules.employee.excel.dto.EmployeeExcelImportDto;
import com.example.hrm.modules.employee.excel.util.EmployeeUtil;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeExcelImportService {

    private final EmployeeUtil employeeUtil;

    public List<EmployeeExcelImportDto> parseExcel(MultipartFile file) {
        List<EmployeeExcelImportDto> dtos = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            dtos = employeeUtil.buildToDto(sheet);

        } catch (Exception e) {
            throw new RuntimeException("Lỗi đọc file Excel: " + e.getMessage(), e);
        }
        return dtos;
    }
}
