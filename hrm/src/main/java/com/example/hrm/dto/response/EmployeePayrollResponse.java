package com.example.hrm.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmployeePayrollResponse {

    private String employeeId;
    private String employeeCode;
    private String fullName;

    private String subDepartment;
    // private String position;
}