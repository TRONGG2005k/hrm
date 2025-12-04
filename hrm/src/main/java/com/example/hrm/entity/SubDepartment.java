package com.example.hrm.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "sub_department")
public class SubDepartment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(nullable = false)
    String name; // tên bộ phận, ví dụ: Backend Team, Frontend Team

    String description;

    @ManyToOne
    @JoinColumn(name = "department_id")
    Department department;

    @Builder.Default
    @Column(nullable = false)
    Boolean isDeleted = false;

    LocalDateTime deletedAt;
}
