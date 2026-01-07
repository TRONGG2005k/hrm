package com.example.hrm.modules.employee.repository;

import com.example.hrm.modules.employee.entity.District;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DistrictRepository extends JpaRepository<District, String> {
    Page<District> findByIsDeletedFalse(Pageable pageable);
    Page<District> findByProvinceIdAndIsDeletedFalse(String provinceId, Pageable pageable);
}
