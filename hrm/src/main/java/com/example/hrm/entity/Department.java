package com.example.hrm.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "department")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(nullable = false, unique = true)
    String name; // Tên phòng ban (VD: Phòng Nhân Sự, Phòng IT)

    String description;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
    Set<SubDepartment> subDepartments;

    @Builder.Default
    @Column(nullable = false)
    Boolean isDeleted = false;

    LocalDateTime deletedAt;
}
