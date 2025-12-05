package com.example.hrm.dto.request;

import com.example.hrm.enums.EmployeeStatus;
import com.example.hrm.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeRequest {

    @NotBlank(message = "Mã nhân viên không được để trống")
    String code;

    @NotBlank(message = "Tên không được để trống")
    String firstName;

    @NotBlank(message = "Họ không được để trống")
    String lastName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate dateOfBirth;

    Gender gender;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    String email;

    String phone;

    EmployeeStatus status;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate joinDate;

    AddressRequest address;

    String subDepartmentId;
}
