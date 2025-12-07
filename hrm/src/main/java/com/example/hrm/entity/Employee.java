package com.example.hrm.entity;

import com.example.hrm.enums.EmployeeStatus;
import com.example.hrm.enums.Gender;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(nullable = false)
    String firstName;

    @Column(nullable = false)
    String lastName;

    LocalDate dateOfBirth;

    @Column(nullable = false, unique = true)
    String code;

    @Enumerated(EnumType.STRING)
    Gender gender;

    @Builder.Default
    LocalDate joinDate = LocalDate.now();

    @Column(nullable = false, unique = true)
    String email;

    String phone;

    @ManyToOne
    @JoinColumn(name = "address_id")
    Address address;

    @Enumerated(EnumType.STRING)
    EmployeeStatus status;

    @OneToMany(mappedBy = "employee")
    @Builder.Default
    List<Contract> contracts = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "sub_department_id")
    SubDepartment subDepartment;

    @Transient
    @Builder.Default
    private List<FileAttachment> files = new ArrayList<>();

    @Builder.Default
    @Column(nullable = false)
    Boolean isDeleted = false;

    LocalDateTime deletedAt;
}
