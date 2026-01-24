package com.example.hrm.modules.employee.excel.mapper;

import com.example.hrm.shared.enums.EmployeeStatus;
import com.example.hrm.shared.enums.Gender;
import com.example.hrm.shared.enums.ShiftType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Mapper cho các enum từ chuỗi tiếng Việt/tiếng Anh
 */
@Component
@Slf4j
public class EnumMapper {

    public Gender mapGender(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        String normalized = value.trim().toLowerCase();
        return switch (normalized) {
            case "nam", "male" -> Gender.MALE;
            case "nữ", "nu", "female" -> Gender.FEMALE;
            case "khác", "other" -> Gender.OTHER;
            default -> {
                log.warn("Unknown gender value: {}", value);
                yield null;
            }
        };
    }

    public EmployeeStatus mapEmployeeStatus(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        String normalized = value.trim().toLowerCase();
        return switch (normalized) {
            case "đang làm", "dang lam", "active" -> EmployeeStatus.ACTIVE;
            case "nghỉ việc", "nghi viec", "inactive", "resigned" -> EmployeeStatus.INACTIVE;
            case "đang nghỉ", "dang nghi", "on_leave" -> EmployeeStatus.ON_LEAVE;
            default -> {
                log.warn("Unknown employee status value: {}", value);
                yield null;
            }
        };
    }

    public ShiftType mapShiftType(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        String normalized = value.trim().toLowerCase();
        return switch (normalized) {
            case "ca sáng", "ca sang", "morning", "full-time" -> ShiftType.MORNING;
            case "ca tối", "ca toi", "night", "part-time" -> ShiftType.NIGHT;
            default -> {
                log.warn("Unknown shift type value: {}", value);
                yield null;
            }
        };
    }
}
