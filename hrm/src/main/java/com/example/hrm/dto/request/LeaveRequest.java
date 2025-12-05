package com.example.hrm.dto.request;

import com.example.hrm.enums.LeaveType;
import com.example.hrm.enums.LeaveStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LeaveRequest {

    @NotBlank(message = "Mã nhân viên không được để trống")
    String employeeId;

    @NotNull(message = "Loại nghỉ không được để trống")
    LeaveType type;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    LocalDate startDate;

    @NotNull(message = "Ngày kết thúc không được để trống")
    LocalDate endDate;

    String reason;

    @NotNull(message = "Trạng thái không được để trống")
    LeaveStatus status;

    String approvalComment;
}
