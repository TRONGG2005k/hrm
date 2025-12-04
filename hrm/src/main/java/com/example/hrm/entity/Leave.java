package com.example.hrm.entity;

import com.example.hrm.enums.LeaveStatus;
import com.example.hrm.enums.LeaveType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "leaves", indexes = {
    @Index(columnList = "employee_id"),
    @Index(columnList = "status"),
    @Index(columnList = "type"),
    @Index(columnList = "startDate,endDate")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Leave {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    Employee employee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    LeaveType type;

    @Column(nullable = false)
    LocalDate startDate;

    @Column(nullable = false)
    LocalDate endDate;

    String reason;  // lý do nghỉ

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    LeaveStatus status;

    @ManyToOne
    @JoinColumn(name = "approved_by_id")
    Employee approvedBy;  // người duyệt (nếu có)

    String approvalComment;  // ghi chú duyệt

    @Builder.Default
    LocalDateTime createdAt = LocalDateTime.now();

    LocalDateTime approvedAt;

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
