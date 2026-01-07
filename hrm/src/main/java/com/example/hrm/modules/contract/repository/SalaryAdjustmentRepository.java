package com.example.hrm.modules.contract.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.hrm.modules.contract.entity.SalaryAdjustment;

@Repository
public interface SalaryAdjustmentRepository extends JpaRepository<SalaryAdjustment, String> {
    Page<SalaryAdjustment> findByIsDeletedFalse(Pageable pageable);
    Page<SalaryAdjustment> findByEmployeeIdAndIsDeletedFalse(String employeeId, Pageable pageable);
    Page<SalaryAdjustment> findByEmployeeIdAndMonthAndIsDeletedFalse(String employeeId, String month, Pageable pageable);
    Page<SalaryAdjustment> findByEmployee_IdAndIsDeletedFalse(String employeeId, Pageable pageable);

    Page<SalaryAdjustment> findByEmployee_IdAndMonthAndIsDeletedFalse(String employeeId, String month, Pageable pageable);

}
