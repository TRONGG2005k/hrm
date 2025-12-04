package com.example.hrm.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import com.example.hrm.enums.PayrollStatus;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "payroll",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = {"employee_id", "month"})
        },
        indexes = {
                @Index(columnList = "employee_id"),
                @Index(columnList = "month"),
                @Index(columnList = "status")
        }
)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Payroll {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    Employee employee;

    @Column(nullable = false, length = 7)
    String month;  // yyyy-MM

    Double baseSalary;
    Double allowance;
    Double overtime;
    Double bonus;
    Double penalty;
    Double unpaidLeave;

    Double totalSalary;

    String note;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    PayrollStatus status;  // DRAFT, PENDING_APPROVAL, APPROVED, REJECTED, PAID

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

