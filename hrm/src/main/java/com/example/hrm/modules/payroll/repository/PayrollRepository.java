package com.example.hrm.modules.payroll.repository;

import com.example.hrm.modules.payroll.entity.Payroll;
import com.example.hrm.shared.enums.PayrollStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PayrollRepository extends JpaRepository<Payroll, String> {
    Page<Payroll> findByIsDeletedFalse(Pageable pageable);
    Page<Payroll> findByEmployeeIdAndIsDeletedFalse(String employeeId, Pageable pageable);
    Optional<Payroll> findByEmployeeIdAndMonthAndIsDeletedFalse(String employeeId, String month);
    boolean existsByEmployeeIdAndMonthAndIsDeletedFalse(String employeeId, String month);
    List<Payroll> findAllByMonthAndStatusAndIsDeletedFalse(String month, PayrollStatus status);
}
