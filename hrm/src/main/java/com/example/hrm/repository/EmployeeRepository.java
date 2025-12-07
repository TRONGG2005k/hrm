package com.example.hrm.repository;

import com.example.hrm.entity.Employee;

import org.springframework.data.domain.Pageable;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {
    Page<Employee> findByIsDeletedFalse(Pageable pageable);

    Optional<Employee> findByIdAndIsDeletedFalse(String id);

    @Query("""
                SELECT e FROM Employee e
                LEFT JOIN FETCH e.subDepartment sd
                LEFT JOIN FileAttachment f ON f.refId = e.id AND f.refType = 'EMPLOYEE'
                WHERE e.id = :employeeId
            """)
    Optional<Employee> findEmployeeWithFiles(@Param("employeeId") String employeeId);

}
