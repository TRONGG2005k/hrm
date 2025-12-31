package com.example.hrm.service;

import com.example.hrm.dto.request.SalaryAdjustmentRequest;
import com.example.hrm.dto.response.SalaryAdjustmentResponse;
import com.example.hrm.entity.Employee;
import com.example.hrm.entity.SalaryAdjustment;
import com.example.hrm.mapper.SalaryAdjustmentMapper;
import com.example.hrm.repository.EmployeeRepository;
import com.example.hrm.repository.SalaryAdjustmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SalaryAdjustmentService {

    private final SalaryAdjustmentRepository adjustmentRepository;
    private final EmployeeRepository employeeRepository;
    private final SalaryAdjustmentMapper mapper;

    public SalaryAdjustmentResponse create(SalaryAdjustmentRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        SalaryAdjustment adjustment = mapper.toEntity(request);
        adjustment.setEmployee(employee);

        adjustmentRepository.save(adjustment);

        return mapper.toResponse(adjustment);
    }

    public Page<SalaryAdjustmentResponse> getAllByEmployeeAndMonth(String employeeId, String month, Pageable pageable) {
        return adjustmentRepository.findByEmployee_IdAndMonthAndIsDeletedFalse(employeeId, month, pageable)
                .map(mapper::toResponse);
    }

    public SalaryAdjustmentResponse update(String id, SalaryAdjustmentRequest request) {
        SalaryAdjustment adjustment = adjustmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Adjustment not found"));

        adjustment.setMonth(request.getMonth());
        adjustment.setType(request.getType());
        adjustment.setAmount(request.getAmount());
        adjustment.setDescription(request.getDescription());

        adjustmentRepository.save(adjustment);

        return mapper.toResponse(adjustment);
    }

    public void delete(String id) {
        SalaryAdjustment adjustment = adjustmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Adjustment not found"));
        adjustment.setIsDeleted(true);
        adjustmentRepository.save(adjustment);
    }
}
