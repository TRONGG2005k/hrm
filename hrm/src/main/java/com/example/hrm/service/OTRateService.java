package com.example.hrm.service;

import com.example.hrm.dto.request.OTRateRequest;
import com.example.hrm.dto.response.OTRateResponse;
import com.example.hrm.entity.OTRate;
import com.example.hrm.enums.OTType;
import com.example.hrm.exception.AppException;
import com.example.hrm.exception.ErrorCode;
import com.example.hrm.mapper.OTRateMapper;
import com.example.hrm.repository.OTRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OTRateService {

    private final OTRateRepository otRateRepository;
    private final OTRateMapper otRateMapper;

    public OTRateResponse create(OTRateRequest request) {

        otRateRepository.findByDateAndTypeAndIsDeletedFalse(request.getDate(), request.getType())
                .ifPresent(e -> {
                    throw new AppException(ErrorCode.OT_RATE_DUPLICATE, 401);
                });

        OTRate otRate = otRateMapper.toEntity(request);
        return otRateMapper.toResponse(otRateRepository.save(otRate));
    }

    public OTRateResponse update(String id, OTRateRequest request) {
        OTRate otRate = otRateRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.OT_RATE_NOT_FOUND, 404));

        otRateMapper.updateEntityFromRequest( request, otRate);
        otRate.setUpdatedAt(LocalDateTime.now());

        return otRateMapper.toResponse(otRateRepository.save(otRate));
    }

    public void delete(String id) {
        OTRate otRate = otRateRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.OT_RATE_NOT_FOUND, 404));

        otRate.setIsDeleted(true);
        otRate.setDeletedAt(LocalDateTime.now());

        otRateRepository.save(otRate);
    }

    public OTRateResponse getById(String id) {
        OTRate otRate = otRateRepository.findById(id)
                .filter(e -> !e.getIsDeleted())
                .orElseThrow(() -> new AppException(ErrorCode.OT_RATE_NOT_FOUND, 404));

        return otRateMapper.toResponse(otRate);
    }

    public List<OTRateResponse> getAll() {
        return otRateRepository.findAllByIsDeletedFalse()
                .stream()
                .map(otRateMapper::toResponse)
                .toList();
    }

    public OTRateResponse getByDateAndType(LocalDate date, OTType type) {
        OTRate otRate = otRateRepository
                .findByDateAndTypeAndIsDeletedFalse(date, type)
                .orElseThrow(() -> new AppException(ErrorCode.OT_RATE_NOT_FOUND, 404));

        return otRateMapper.toResponse(otRate);
    }
}
