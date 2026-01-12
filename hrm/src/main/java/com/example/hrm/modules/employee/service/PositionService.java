package com.example.hrm.modules.employee.service;


import com.example.hrm.modules.employee.dto.request.PositionRequest;
import com.example.hrm.modules.employee.dto.response.PositionResponse;
import com.example.hrm.modules.employee.entity.Position;
import com.example.hrm.modules.employee.mapper.PositionMapper;
import com.example.hrm.modules.employee.repository.PositionRepository;
import com.example.hrm.shared.exception.AppException;
import com.example.hrm.shared.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PositionService {
    private final PositionRepository repository;
    private final PositionMapper mapper;

    public PositionResponse create(PositionRequest request) {
        if (repository.existsByCode(request.code())) {
            throw new AppException(ErrorCode.DUPLICATE_CODE, 500);
        }

        Position position = mapper.toEntity(request);
        return mapper.toResponse(repository.save(position));
    }

    public PositionResponse update(String id, PositionRequest request) {
        Position position = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, 404));

        position.setName(request.name());
        position.setDescription(request.description());
        if (request.active() != null) {
            position.setActive(request.active());
        }

        return mapper.toResponse(repository.save(position));
    }

    public List<PositionResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    public PositionResponse getById(String id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, 404));
    }
}
