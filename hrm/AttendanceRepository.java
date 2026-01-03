package com.example.hrm.modules.attendance.repository;

import com.example.hrm.modules.attendance.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Attendance Repository - Data Access Layer cho Attendance Entity
 * 
 * Module: Attendance (Chấm công)
 * 
 * Cung cấp các phương thức để truy vấn dữ liệu chấm công từ database
 * 
 * @author HRM Team
 * @version 1.0
 */
@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    
    /**
     * Tìm danh sách chấm công của một nhân viên
     */
    List<Attendance> findByEmployeeId(Long employeeId);
    
    /**
     * Tìm chấm công của nhân viên trong một ngày cụ thể
     */
    Optional<Attendance> findByEmployeeIdAndAttendanceDate(Long employeeId, String date);
    
    /**
     * Tìm danh sách chấm công trong khoảng thời gian
     */
    @Query("SELECT a FROM Attendance a WHERE a.employeeId = :employeeId " +
           "AND a.createdAt BETWEEN :startDate AND :endDate")
    List<Attendance> findByEmployeeIdAndDateRange(
            @Param("employeeId") Long employeeId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    /**
     * Đếm số lần chấm công của nhân viên
     */
    long countByEmployeeId(Long employeeId);
}
