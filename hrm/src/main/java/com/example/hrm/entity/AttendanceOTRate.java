package com.example.hrm.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "attendance_ot_rate")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class AttendanceOTRate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "attendance_id", nullable = false)
    Attendance attendance;

    @ManyToOne
    @JoinColumn(name = "ot_rate_id", nullable = false)
    OTRate otRate;

    Double otHours; // số giờ tăng ca

    @Builder.Default
    LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @Column(nullable = false)
    Boolean isDeleted = false;

    LocalDateTime deletedAt;
}
