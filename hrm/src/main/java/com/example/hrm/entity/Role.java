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
    public class Role {
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        String id;

        String name;

        String description;

        @ManyToMany
        @JoinTable(
                name = "role_permission",
                joinColumns = @JoinColumn( name = "role_id"),
                inverseJoinColumns = @JoinColumn(name =  "permission_id")
        )
        Set<Permission> permissions;

        @Builder.Default
        @Column(nullable = false)
        Boolean isDeleted = false;

        LocalDateTime deletedAt;
    }
