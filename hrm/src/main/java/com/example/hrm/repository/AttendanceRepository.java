package com.example.hrm.repository;

import com.example.hrm.entity.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, String> {
    Page<Attendance> findByIsDeletedFalse(Pageable pageable);
    Page<Attendance> findByEmployeeIdAndIsDeletedFalse(String employeeId, Pageable pageable);
    Page<Attendance> findByDateAndIsDeletedFalse(LocalDate date, Pageable pageable);
}
