package com.example.hrm.modules.penalty.repository;

import com.example.hrm.modules.penalty.entity.PenaltyRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PenaltyRuleRepository extends JpaRepository<PenaltyRule, String> {

    Optional<PenaltyRule> findByCode(String code);

    List<PenaltyRule> findAllByActiveTrueOrderByPriorityAsc();
}
