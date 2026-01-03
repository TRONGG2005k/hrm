package com.example.hrm.service;

import com.example.hrm.dto.request.PenaltyRuleRequest;
import com.example.hrm.dto.response.PenaltyRuleResponse;
import com.example.hrm.entity.PenaltyRule;
import com.example.hrm.exception.AppException;
import com.example.hrm.exception.ErrorCode;
import com.example.hrm.mapper.PenaltyRuleMapper;
import com.example.hrm.repository.PenaltyRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PenaltyRuleServiceImpl implements PenaltyRuleService {

    private final PenaltyRuleRepository penaltyRuleRepository;
    private final PenaltyRuleMapper penaltyRuleMapper;

    @Override
    public PenaltyRuleResponse create(PenaltyRuleRequest request) {
        PenaltyRule penaltyRule = penaltyRuleMapper.toEntity(request);
        penaltyRule.setCreatedAt(LocalDateTime.now());
        penaltyRule.setUpdatedAt(LocalDateTime.now());
        
        return penaltyRuleMapper.toResponse(penaltyRuleRepository.save(penaltyRule));
    }

    @Override
    public PenaltyRuleResponse update(String id, PenaltyRuleRequest request) {
        PenaltyRule penaltyRule = penaltyRuleRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PENALTY_RULE_NOT_FOUND, 404));

        penaltyRuleMapper.updateEntityFromRequest(request, penaltyRule);
        penaltyRule.setUpdatedAt(LocalDateTime.now());

        return penaltyRuleMapper.toResponse(penaltyRuleRepository.save(penaltyRule));
    }

    @Override
    public void delete(String id) {
        PenaltyRule penaltyRule = penaltyRuleRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PENALTY_RULE_NOT_FOUND, 404));

        penaltyRuleRepository.deleteById(id);
    }

    @Override
    public PenaltyRuleResponse getById(String id) {
        PenaltyRule penaltyRule = penaltyRuleRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PENALTY_RULE_NOT_FOUND, 404));

        return penaltyRuleMapper.toResponse(penaltyRule);
    }

    @Override
    public List<PenaltyRuleResponse> getAll() {
        return penaltyRuleRepository.findAll()
                .stream()
                .map(penaltyRuleMapper::toResponse)
                .toList();
    }

    @Override
    public List<PenaltyRuleResponse> getAllActive() {
        return penaltyRuleRepository.findAllByActiveTrueOrderByPriorityAsc()
                .stream()
                .map(penaltyRuleMapper::toResponse)
                .toList();
    }
}
