package com.example.hrm.modules.organization.excel.dto;

import lombok.Data;

@Data
public class SubDepartmentExcelDto {
    private String code;
    private String name;
    private String departmentCode; // liên kết Department
    private String description;
    private String status; // ACTIVE / INACTIVE
}
