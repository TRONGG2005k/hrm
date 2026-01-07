package com.example.hrm.modules.contract.repository;

import com.example.hrm.modules.contract.entity.Contract;
import com.example.hrm.shared.enums.ContractStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<Contract, String> {
    @EntityGraph(attributePaths = {"employee"})
    Page<Contract> findByIsDeletedFalseAndStatus(Pageable pageable, ContractStatus status);
    Page<Contract> findByEmployeeIdAndIsDeletedFalse(String employeeId, Pageable pageable);
    Optional<Contract> findByIdAndIsDeletedFalse(String id);
    Page<Contract> findByIsDeletedFalseAndStatusNot(Pageable pageable, ContractStatus status);
}
