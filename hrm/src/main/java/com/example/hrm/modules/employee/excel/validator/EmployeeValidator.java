package com.example.hrm.modules.employee.excel.validator;

import com.example.hrm.modules.employee.excel.dto.EmployeeExcelImportDto;
import com.example.hrm.modules.employee.excel.util.EmployeeUtil;
import com.example.hrm.modules.employee.repository.EmployeeRepository;
import com.example.hrm.modules.employee.repository.PositionRepository;
import com.example.hrm.modules.organization.repository.SubDepartmentRepository;
import com.example.hrm.shared.enums.EmployeeStatus;
import com.example.hrm.shared.enums.Gender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor

public class EmployeeValidator {

    private final EmployeeUtil employeeUtil;
    private final SubDepartmentRepository subDepartmentRepository;
    private final PositionRepository positionRepository;
    private final EmployeeRepository employeeRepository;

    public List<String> validateEmployee(EmployeeExcelImportDto dto, int rowNumber) {
        List<String> errors = new ArrayList<>();

        // Code
        if (employeeUtil.isBlank(dto.getCode())) {
            errors.add(error(rowNumber, "Code không được để trống"));
        } else if (employeeRepository.existsByCodeAndIsDeletedFalse(dto.getCode())) {
            errors.add(error(rowNumber, "Mã nhân viên đã tồn tại: " + dto.getCode()));
        }

        // First name
        if (employeeUtil.isBlank(dto.getFirstName())) {
            errors.add(error(rowNumber, "First name không được để trống"));
        }

        // Last name
        if (employeeUtil.isBlank(dto.getLastName())) {
            errors.add(error(rowNumber, "Last name không được để trống"));
        }

        // Email
        if (employeeUtil.isBlank(dto.getEmail())) {
            errors.add(error(rowNumber, "Email không được để trống"));
        } else if (!employeeUtil.isValidEmail(dto.getEmail())) {
            errors.add(error(rowNumber, "Email không hợp lệ"));
        }

        // Phone
        if (!employeeUtil.isBlank(dto.getPhone()) && !employeeUtil.isValidPhone(dto.getPhone())) {
            errors.add(error(rowNumber, "Số điện thoại không hợp lệ"));
        }

        // Gender
        if (employeeUtil.isBlank(dto.getGender())) {
            errors.add(error(rowNumber, "Gender không được để trống"));
        } else if (!employeeUtil.isValidEnum(Gender.class, dto.getGender())) {
            errors.add(error(rowNumber, "Gender không hợp lệ"));
        }

        // Status
        if (!employeeUtil.isBlank(dto.getStatus())
                && !employeeUtil.isValidEnum(EmployeeStatus.class, dto.getStatus())) {
            errors.add(error(rowNumber, "Status không hợp lệ"));
        }

        // Date of birth
        if (dto.getDateOfBirth() != null && employeeUtil.isFutureDate(dto.getDateOfBirth())) {
            errors.add(error(rowNumber, "Ngày sinh không được lớn hơn ngày hiện tại"));
        }

        // Join date
        if (dto.getJoinDate() != null && employeeUtil.isFutureDate(dto.getJoinDate())) {
            errors.add(error(rowNumber, "Ngày vào làm không được lớn hơn ngày hiện tại"));
        }

        // Department
        if (employeeUtil.isBlank(dto.getDepartmentName())) {
            errors.add(error(rowNumber, "Tên phòng ban không được để trống"));
        } else if (!subDepartmentRepository.existsByNameAndIsDeletedFalse(dto.getDepartmentName())) {
            errors.add(error(rowNumber, "Không tồn tại phòng ban: " + dto.getDepartmentName()));
        }

        // Position
        if (employeeUtil.isBlank(dto.getPositionName())) {
            errors.add(error(rowNumber, "Tên chức vụ không được để trống"));
        } else if (!positionRepository.existsByNameAndIsDeletedFalse(dto.getPositionName())) {
            errors.add(error(rowNumber, "Không tồn tại chức vụ: " + dto.getPositionName()));
        }

        return errors;
    }

    // ================== PRIVATE HELPERS ==================

    private String error(int rowNumber, String message) {
        return "Dòng " + rowNumber + ": " + message;
    }
}

