package com.example.hrm.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import com.example.hrm.enums.ContactType;

import java.time.LocalDateTime;

@Entity
@Table(name = "contacts", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"employee_id", "type", "value"})
}, indexes = {
    @Index(columnList = "employee_id"),
    @Index(columnList = "type")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    Employee employee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    ContactType type;

    @Column(nullable = false)
    String value;

    String relation;

    String note;

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
