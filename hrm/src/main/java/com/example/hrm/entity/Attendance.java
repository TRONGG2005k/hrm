package com.example.hrm.entity;

import com.example.hrm.enums.AttendanceStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "attendance", indexes = {
        @Index(columnList = "employee_id"),
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

    LocalDateTime checkInTime;
    LocalDateTime checkOutTime;

    @Enumerated(EnumType.STRING)
    AttendanceStatus status;

    @OneToMany(
            mappedBy = "attendance",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    List<BreakTime> breaks;

//    @Column(name = "work_date", nullable = false)
    @Column(name = "work_date", nullable = true)
    LocalDate workDate;

    // Quan hệ OT
    @OneToMany(mappedBy = "attendance", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    List<AttendanceOTRate> attendanceOTRates = new ArrayList<>();

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

