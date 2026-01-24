package com.example.hrm.modules.employee.excel.exception;


import java.util.List;

public class ExcelImportException extends RuntimeException {

    private final List<String> errors;

    public ExcelImportException(List<String> errors) {
        super("Import Excel thất bại");
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}
