package com.example.hrm.modules.contract.service;

import com.example.hrm.modules.contract.dto.request.AllowanceRequest;
import com.example.hrm.modules.contract.dto.response.AllowanceResponse;
import com.example.hrm.modules.contract.entity.Allowance;
import com.example.hrm.modules.contract.mapper.AllowanceMapper;
import com.example.hrm.modules.contract.repository.AllowanceRepository;
import com.example.hrm.shared.exception.AppException;
import com.example.hrm.shared.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AllowanceService {

    private final AllowanceRepository repository;
    private final AllowanceMapper mapper;

    public AllowanceResponse create(AllowanceRequest request) {

        if (repository.existsByCodeAndActiveTrue(request.getCode())) {
            throw new AppException(
                    ErrorCode.ALLOWANCE_CODE_EXISTS, 400
            );
        }

        Allowance allowance = mapper.toEntity(request);
        allowance.setActive(true);

        return mapper.toResponse(repository.save(allowance));
    }

    public List<AllowanceResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }
}
