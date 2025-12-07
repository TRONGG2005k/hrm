package com.example.hrm.service;

import com.example.hrm.dto.request.DistrictRequest;
import com.example.hrm.dto.response.DistrictResponse;
import com.example.hrm.entity.District;
import com.example.hrm.entity.Province;
import com.example.hrm.exception.AppException;
import com.example.hrm.exception.ErrorCode;
import com.example.hrm.mapper.DistrictMapper;
import com.example.hrm.repository.DistrictRepository;
import com.example.hrm.repository.ProvinceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DistrictService {

    private final DistrictRepository districtRepository;
    private final ProvinceRepository provinceRepository;
    private final DistrictMapper districtMapper;

    @Transactional
    public DistrictResponse createDistrict(DistrictRequest request) {
        Province province = provinceRepository.findById(request.getProvinceId())
                .filter(p -> !p.getIsDeleted())
                .orElseThrow(() -> new AppException(ErrorCode.PROVINCE_NOT_FOUND, 404));

        District district = districtMapper.toEntity(request);
        district.setProvince(province);
        districtRepository.save(district);
        return districtMapper.toResponse(district);
    }

    @Transactional
    public Page<DistrictResponse> getAllDistricts(Pageable pageable) {
        return districtRepository.findByIsDeletedFalse(pageable)
                .map(districtMapper::toResponse);
    }

    @Transactional
    public Page<DistrictResponse> getDistrictsByProvince(String provinceId, Pageable pageable) {
        return districtRepository.findByProvinceIdAndIsDeletedFalse(provinceId, pageable)
                .map(districtMapper::toResponse);
    }

    @Transactional
    public DistrictResponse getDistrictById(String id) {
        District district = districtRepository.findById(id)
                .filter(d -> !d.getIsDeleted())
                .orElseThrow(() -> new AppException(ErrorCode.DISTRICT_NOT_FOUND, 404));
        return districtMapper.toResponse(district);
    }

    @Transactional
    public DistrictResponse updateDistrict(String id, DistrictRequest request) {
        District district = districtRepository.findById(id)
                .filter(d -> !d.getIsDeleted())
                .orElseThrow(() -> new AppException(ErrorCode.DISTRICT_NOT_FOUND, 404));

        Province province = provinceRepository.findById(request.getProvinceId())
                .filter(p -> !p.getIsDeleted())
                .orElseThrow(() -> new AppException(ErrorCode.PROVINCE_NOT_FOUND, 404));

        districtMapper.updateEntity(request, district);
        district.setProvince(province);
        districtRepository.save(district);
        return districtMapper.toResponse(district);
    }

    @Transactional
    public void deleteDistrict(String id) {
        District district = districtRepository.findById(id)
                .filter(d -> !d.getIsDeleted())
                .orElseThrow(() -> new AppException(ErrorCode.DISTRICT_NOT_FOUND, 404));
        district.setIsDeleted(true);
        district.setDeletedAt(java.time.LocalDateTime.now());
        districtRepository.save(district);
    }
}

