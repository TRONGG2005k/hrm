package com.example.hrm.modules.leave.repository;

import com.example.hrm.modules.leave.entity.LeaveRequest;
import com.example.hrm.shared.enums.LeaveStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, String> {
    Optional<LeaveRequest> findByIdAndIsDeletedFalse(String id);

    Page<LeaveRequest> findAllByIsDeletedFalse(Pageable pageable );
    Page<LeaveRequest> findAllByStatusAndIsDeletedFalse(Pageable pageable, LeaveStatus status);
}
