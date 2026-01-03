package com.example.hrm.repository;

import com.example.hrm.entity.PenaltyRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PenaltyRuleRepository extends JpaRepository<PenaltyRule, String> {

    Optional<PenaltyRule> findByCode(String code);

    List<PenaltyRule> findAllByActiveTrueOrderByPriorityAsc();
}
