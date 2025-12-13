package com.example.hrm.service;

import com.example.hrm.dto.request.UserAccountRequest;
import com.example.hrm.dto.response.UserAccountResponse;
import com.example.hrm.enums.Role;
import com.example.hrm.exception.AppException;
import com.example.hrm.exception.ErrorCode;
import com.example.hrm.mapper.RoleMapper;
import com.example.hrm.mapper.UserAccountMapper;
import com.example.hrm.repository.EmployeeRepository;
import com.example.hrm.repository.RoleRepository;
import com.example.hrm.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserAccountService {
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserAccountMapper userAccountMapper;
    private final EmployeeRepository employeeRepository;
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public UserAccountResponse create(UserAccountRequest request){
        var user = userAccountMapper.toEntity(request);
        var employee = employeeRepository
                .findByIdAndIsDeletedFalse(request.getEmployeeId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, 404));
        user.setEmployee(employee);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        var role = roleRepository.findByNameAndIsDeletedFalse(Role.ROLE_EMPLOYEE.name())
                .orElseGet(() -> roleRepository.save(com.example.hrm.entity.Role.builder()
                                .name(Role.ROLE_EMPLOYEE.name()).build()));
        user.getRoles().add(role);

        userAccountRepository.save(user);
        var response = userAccountMapper.toResponse(user);
        var roles = user.getRoles().stream().map(roleMapper::toResponse).toList();
        response.setRoles(roles);
        return response;
    }

    public Page<UserAccountResponse> getAll(int page, int size){
        var accounts = userAccountRepository.findByIsDeletedFalse(PageRequest.of(page, size));
        return accounts.map(item -> {
            var roles = item.getRoles().stream().map(roleMapper::toResponse).toList();
            var response = userAccountMapper.toResponse(item);
            response.setRoles(roles);
            return response;
        });
    }

    public UserAccountResponse getById(String id) {
        var user = userAccountRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, 404));

        var response = userAccountMapper.toResponse(user);
        var roles = user.getRoles().stream().map(roleMapper::toResponse).toList();
        response.setRoles(roles);

        return response;
    }

    public UserAccountResponse update(String id, UserAccountRequest request) {
        var user = userAccountRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, 404));
        // Update employee
        if (request.getEmployeeId() != null) {
            var employee = employeeRepository.findByIdAndIsDeletedFalse(request.getEmployeeId())
                    .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_FOUND, 404));

            user.setEmployee(employee);
        }
        // Update password nếu FE truyền
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        // Các field khác như username, email (nếu có):
        if (request.getUsername() != null) user.setUsername(request.getUsername());
        userAccountRepository.save(user);
        // Build response
        var response = userAccountMapper.toResponse(user);
        var roles = user.getRoles().stream().map(roleMapper::toResponse).toList();
        response.setRoles(roles);

        return response;
    }

    @Transactional
    public void delete(String id) {
        var user = userAccountRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, 404));

        user.setDeletedAt(LocalDateTime.now());
        user.setIsDeleted(true);

        userAccountRepository.save(user);
    }



}
