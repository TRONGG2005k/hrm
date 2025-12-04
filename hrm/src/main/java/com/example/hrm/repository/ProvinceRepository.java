package com.example.hrm.repository;

import com.example.hrm.entity.Province;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProvinceRepository extends JpaRepository<Province, String> {
    Page<Province> findByIsDeletedFalse(Pageable pageable);
}
