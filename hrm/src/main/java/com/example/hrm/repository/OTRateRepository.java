package com.example.hrm.repository;

import com.example.hrm.entity.OTRate;
import com.example.hrm.enums.OTType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OTRateRepository extends JpaRepository<OTRate, String> {
    Page<OTRate> findByIsDeletedFalse(Pageable pageable);
    Page<OTRate> findByTypeAndIsDeletedFalse(OTType type, Pageable pageable);
}
