package com.example.hrm.repository;

import com.example.hrm.entity.Payroll;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PayrollRepository extends JpaRepository<Payroll, String> {
    Page<Payroll> findByIsDeletedFalse(Pageable pageable);
    Page<Payroll> findByEmployeeIdAndIsDeletedFalse(String employeeId, Pageable pageable);
    Payroll findByEmployeeIdAndMonthAndIsDeletedFalse(String employeeId, String month);
}
