
package com.example.hrm.repository;

import com.example.hrm.entity.Attendance;
import com.example.hrm.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, String> {
    Page<Attendance> findByIsDeletedFalse(Pageable pageable);

    @Query("""
        select a
        from Attendance a
        join a.employee e
        join e.subDepartment s
        where s.id = :subDepartmentId
          and a.isDeleted = false
    """)
    Page<Attendance> findBySubDepartmentId(
            Pageable pageable,
            @Param("subDepartmentId") String subDepartmentId
    );


    Optional<Attendance> findTopByEmployeeOrderByCheckInTimeDesc(Employee employee);

    Optional<Attendance> findByIdAndIsDeletedFalse(String id);

    Optional<Attendance> findTopByEmployeeAndCheckOutTimeIsNullOrderByCheckInTimeDesc(Employee employee );

    Page<Attendance> findByEmployeeIdAndIsDeletedFalse(String employeeId, Pageable pageable);
}
