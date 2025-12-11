package com.example.hrm.repository;

import com.example.hrm.entity.Contract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<Contract, String> {
    @EntityGraph(attributePaths = {"employee"})
    Page<Contract> findByIsDeletedFalse(Pageable pageable);
    Page<Contract> findByEmployeeIdAndIsDeletedFalse(String employeeId, Pageable pageable);
    Optional<Contract> findByIdAndIsDeletedFalse(String id);
}
