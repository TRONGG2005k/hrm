package com.example.hrm.service;

import com.example.hrm.entity.Attendance;
import com.example.hrm.entity.AttendancePenalty;
import com.example.hrm.enums.PenaltyType;
import com.example.hrm.repository.AttendancePenaltyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendancePenaltyService {

    private final AttendancePenaltyRepository penaltyRepository;
    private final AttendanceHelper attendanceHelper;

    // ===== RULE CONFIG (sau này đưa DB) =====
    private static final int LATE_GRACE_MINUTES = 5;
    private static final int EARLY_GRACE_MINUTES = 5;

    public void calculateAndSave(Attendance attendance) {

        // Chưa checkout thì không xử lý
        if (attendance.getCheckInTime() == null
                || attendance.getCheckOutTime() == null) {
            return;
        }

        // Tránh tạo penalty trùng
        if (penaltyRepository.existsByAttendance_Id(attendance.getId())) {
            return;
        }

        long lateMinutes = attendanceHelper.calculateLateMinutes(
                attendance,
                LATE_GRACE_MINUTES
        );

        long earlyLeaveMinutes = attendanceHelper.calculateEarlyLeaveMinutes(
                attendance,
                EARLY_GRACE_MINUTES
        );

        // Không có vi phạm
        if (lateMinutes == 0 && earlyLeaveMinutes == 0) {
            return;
        }

        AttendancePenalty penalty = new AttendancePenalty();
        penalty.setAttendance(attendance);
        penalty.setLateMinutes(lateMinutes);
        penalty.setEarlyLeaveMinutes(earlyLeaveMinutes);

        // Chỉ ghi nhận vi phạm – KHÔNG tính tiền
        penalty.setPenaltyType(PenaltyType.TIME);
        penalty.setReason(buildReason(lateMinutes, earlyLeaveMinutes));
        penalty.setCreatedAt(LocalDateTime.now());

        penaltyRepository.save(penalty);

        log.info(
                "Attendance {} penalty recorded: late={} early={}",
                attendance.getId(),
                lateMinutes,
                earlyLeaveMinutes
        );
    }

    private String buildReason(long late, long early) {
        StringBuilder sb = new StringBuilder();
        if (late > 0) {
            sb.append("Đi muộn ").append(late).append(" phút. ");
        }
        if (early > 0) {
            sb.append("Về sớm ").append(early).append(" phút.");
        }
        return sb.toString().trim();
    }
}
