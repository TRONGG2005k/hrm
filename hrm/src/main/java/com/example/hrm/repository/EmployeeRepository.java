package com.example.hrm.repository;

import com.example.hrm.entity.Employee;

import org.springframework.data.domain.Pageable;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {
    Page<Employee> findByIsDeletedFalse(Pageable pageable);
    Optional<Employee> findByIdAndIsDeletedFalse(String id);
}

