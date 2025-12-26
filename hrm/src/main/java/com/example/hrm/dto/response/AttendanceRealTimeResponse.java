package com.example.hrm.dto.response;

import com.example.hrm.enums.AttendanceStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttendanceRealTimeResponse {

    String employeeCode;
    String employeeName;

    LocalDateTime time; // thời điểm quét (check-in / check-out)

    AttendanceStatus status;
    // WORKING / COMPLETED / LATE (tuỳ bạn định nghĩa)

    String message; // "Check-in thành công", "Đi trễ", "Check-out thành công"

}
