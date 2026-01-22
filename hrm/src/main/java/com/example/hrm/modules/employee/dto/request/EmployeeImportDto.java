package com.example.hrm.modules.employee.dto.request;

import com.example.hrm.shared.enums.EmployeeStatus;
import com.example.hrm.shared.enums.Gender;
import com.example.hrm.shared.enums.ShiftType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

/**
 * DTO for importing employee data from Excel
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeImportDto {
    String code;
    String firstName;
    String lastName;
    String dateOfBirth; // format: YYYY-MM-DD
    String gender;
    String email;
    String phone;
    String status;
    String joinDate; // format: YYYY-MM-DD
    String shiftType;
    String addressId;
    String subDepartmentId;
    String positionId;

    // For validation tracking
    Integer rowNumber;
}
