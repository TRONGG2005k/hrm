package com.example.hrm.entity;

import com.example.hrm.enums.AdjustmentType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "salary_adjustment")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SalaryAdjustment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
            @JoinColumn(name = "employee_id", nullable = false)
    Employee employee;

    @Column(nullable = false)
    String month; // "2025-03"

    @Enumerated(EnumType.STRING)
    AdjustmentType type;

    @Column(nullable = false)
    Double amount;

    String description;

    @Builder.Default
    LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @Column(nullable = false)
    Boolean isDeleted = false;

    LocalDateTime deletedAt;
}
