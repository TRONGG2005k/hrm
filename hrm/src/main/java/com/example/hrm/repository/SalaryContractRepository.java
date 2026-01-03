package com.example.hrm.repository;

import com.example.hrm.entity.SalaryContract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SalaryContractRepository extends JpaRepository<SalaryContract, String> {
    Page<SalaryContract> findByIsDeletedFalse(Pageable pageable);
    Page<SalaryContract> findByEmployeeIdAndIsDeletedFalse(String employeeId, Pageable pageable);
    Page<SalaryContract> findByContractIdAndIsDeletedFalse(String contractId, Pageable pageable);
    Page<SalaryContract> findByEmployee_IdAndIsDeletedFalse(String employeeId, Pageable pageable);
    Optional<SalaryContract>findByEmployee_IdAndContract_IdAndIsDeletedFalse(String employeeId, String contractId);
    Optional<SalaryContract> findByEmployeeIdAndIsDeletedFalse(String employeeId);
}
