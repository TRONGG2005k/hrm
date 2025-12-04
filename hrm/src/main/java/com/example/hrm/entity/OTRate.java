package com.example.hrm.entity;

import com.example.hrm.enums.OTType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ot_rate")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class OTRate {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    LocalDate date; // Ngày áp dụng
    @Enumerated(EnumType.STRING)
    OTType type; // NORMAL, WEEKEND, HOLIDAY, NIGHT

    Double rate; // 1.5, 2.0, 3.0
    String description;

    @Builder.Default
    LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @Column(nullable = false)
    Boolean isDeleted = false;

    LocalDateTime deletedAt;
}
