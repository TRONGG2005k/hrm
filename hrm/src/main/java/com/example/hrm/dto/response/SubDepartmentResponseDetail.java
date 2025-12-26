package com.example.hrm.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubDepartmentResponseDetail {

    String id;

    String departmentId;

    String name;

    String description;

    List<EmployeeResponse> employeeResponses;
}
