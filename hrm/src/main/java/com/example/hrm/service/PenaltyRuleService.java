package com.example.hrm.service;

import com.example.hrm.dto.request.PenaltyRuleRequest;
import com.example.hrm.dto.response.PenaltyRuleResponse;

import java.util.List;

public interface PenaltyRuleService {

    PenaltyRuleResponse create(PenaltyRuleRequest request);

    PenaltyRuleResponse update(String id, PenaltyRuleRequest request);

    void delete(String id);

    PenaltyRuleResponse getById(String id);

    List<PenaltyRuleResponse> getAll();

    List<PenaltyRuleResponse> getAllActive();
}
