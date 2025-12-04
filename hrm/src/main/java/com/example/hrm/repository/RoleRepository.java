package com.example.hrm.repository;

import com.example.hrm.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
    Page<Role> findByIsDeletedFalse(Pageable pageable);
    Role findByNameAndIsDeletedFalse(String name);
}
