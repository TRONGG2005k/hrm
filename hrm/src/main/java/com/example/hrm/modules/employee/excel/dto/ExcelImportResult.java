package com.example.hrm.modules.employee.excel.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ExcelImportResult {
    private int successCount;
    private List<String> errors;
}

