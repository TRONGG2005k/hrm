package com.example.hrm.repository;

import com.example.hrm.entity.SubDepartment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubDepartmentRepository extends JpaRepository<SubDepartment, String> {
    Page<SubDepartment> findByIsDeletedFalse(Pageable pageable);
    Page<SubDepartment> findByDepartmentIdAndIsDeletedFalse(String departmentId, Pageable pageable);
}
