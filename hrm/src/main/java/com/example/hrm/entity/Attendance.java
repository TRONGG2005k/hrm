package com.example.hrm.entity;

import com.example.hrm.enums.AttendanceStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "attendance", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"employee_id", "date"})
}, indexes = {
        @Index(columnList = "employee_id"),
        @Index(columnList = "date"),
        @Index(columnList = "status")
})
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    Employee employee;

    @Column(nullable = false)
    LocalDate date;

    // Thời gian vào ra thực tế
    LocalDateTime checkInTime;
    LocalDateTime checkOutTime;

    // Số giờ làm việc được tính tự động từ checkIn/checkOut - lunchHours
    @Column(nullable = false)
    Double workingHours = 0.0;

    @Column(nullable = false)
    Double lunchHours = 0.0;

    // Trễ / về sớm (tính tự động)
    @Column(nullable = false)
    Integer lateMinutes = 0;

    @Column(nullable = false)
    Integer earlyLeaveMinutes = 0;

    @Enumerated(EnumType.STRING)
    AttendanceStatus status;

    // Quan hệ OT
    @OneToMany(mappedBy = "attendance", cascade = CascadeType.ALL, orphanRemoval = true)
    List<AttendanceOTRate> attendanceOTRates;

    @Builder.Default
    LocalDateTime createdAt = LocalDateTime.now();

    LocalDateTime updatedAt;

    @Builder.Default
    @Column(nullable = false)
    Boolean isDeleted = false;

    LocalDateTime deletedAt;

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

