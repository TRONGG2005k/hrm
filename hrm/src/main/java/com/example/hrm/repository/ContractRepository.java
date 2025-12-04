package com.example.hrm.repository;

import com.example.hrm.entity.Contract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractRepository extends JpaRepository<Contract, String> {
    Page<Contract> findByIsDeletedFalse(Pageable pageable);
    Page<Contract> findByEmployeeIdAndIsDeletedFalse(String employeeId, Pageable pageable);
}
