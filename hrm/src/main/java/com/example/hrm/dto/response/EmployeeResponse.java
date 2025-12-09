package com.example.hrm.dto.response;

import com.example.hrm.enums.EmployeeStatus;
import com.example.hrm.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeResponse {

    String id;

    String code;

    String firstName;

    String lastName;

    LocalDate dateOfBirth;

    Gender gender;

    String email;

    String phone;

    List<FileAttachmentResponse> fileAttachmentResponses;

    List<ContactResponse> contacts;

    EmployeeStatus status;

    LocalDate joinDate;

    AddressResponse address;

    SubDepartmentResponse subDepartment;
    
    Boolean isDeleted;

    LocalDateTime deletedAt;
}
