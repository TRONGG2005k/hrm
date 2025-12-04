package com.example.hrm.repository;

import com.example.hrm.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, String> {
    Page<Department> findByIsDeletedFalse(Pageable pageable);
    Department findByNameAndIsDeletedFalse(String name);
}
