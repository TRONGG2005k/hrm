package com.example.hrm.repository;

import com.example.hrm.entity.Leave;
import com.example.hrm.enums.LeaveStatus;
import com.example.hrm.enums.LeaveType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, String> {
    Page<Leave> findByIsDeletedFalse(Pageable pageable);
    Page<Leave> findByEmployeeIdAndIsDeletedFalse(String employeeId, Pageable pageable);
    Page<Leave> findByStatusAndIsDeletedFalse(LeaveStatus status, Pageable pageable);
    Page<Leave> findByTypeAndIsDeletedFalse(LeaveType type, Pageable pageable);
    Page<Leave> findByEmployeeIdAndTypeAndIsDeletedFalse(String employeeId, LeaveType type, Pageable pageable);
    Page<Leave> findByStartDateGreaterThanEqualAndEndDateLessThanEqualAndIsDeletedFalse(
            LocalDate startDate, LocalDate endDate, Pageable pageable);
}
