package com.example.hrm.service;

import com.example.hrm.dto.request.RoleRequest;
import com.example.hrm.dto.response.RoleResponse;
import com.example.hrm.entity.Role;
import com.example.hrm.exception.AppException;
import com.example.hrm.exception.ErrorCode;
import com.example.hrm.mapper.RoleMapper;
import com.example.hrm.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public RoleResponse create(RoleRequest request) {
        var role = roleMapper.toEntity(request);
        roleRepository.save(role);
        return roleMapper.toResponse(role);
    }

    public Page<RoleResponse> getAll(int page, int size) {
        var roles = roleRepository.findByIsDeletedFalse(PageRequest.of(page, size));
        return roles.map(roleMapper::toResponse);
    }

    public RoleResponse getById(String id) {
        var role = roleRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND, 404));
        return roleMapper.toResponse(role);
    }

    public RoleResponse update(String id, RoleRequest request) {
        var role = roleRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND, 404));

        role.setName(request.getName());
        role.setDescription(request.getDescription());

        roleRepository.save(role);
        return roleMapper.toResponse(role);
    }

    public void delete(String id) {
        var role = roleRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND, 404));

        role.setIsDeleted(true);
        role.setDeletedAt(java.time.LocalDateTime.now());
        roleRepository.save(role);
    }
}
