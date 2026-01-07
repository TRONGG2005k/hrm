package com.example.hrm.modules.contract.service;

import com.example.hrm.modules.contract.dto.request.ContractRequest;
import com.example.hrm.modules.contract.dto.request.ContractUpdateRequest;
import com.example.hrm.modules.contract.dto.response.ContractListResponse;
import com.example.hrm.modules.contract.dto.response.ContractResponse;
import com.example.hrm.modules.contract.entity.SalaryContract;
import com.example.hrm.shared.enums.ContractStatus;
import com.example.hrm.shared.enums.RefType;
import com.example.hrm.shared.exception.AppException;
import com.example.hrm.shared.exception.ErrorCode;
import com.example.hrm.modules.contract.mapper.ContractMapper;
import com.example.hrm.modules.file.mapper.FileAttachmentMapper;
import com.example.hrm.modules.contract.repository.ContractRepository;
import com.example.hrm.modules.employee.repository.EmployeeRepository;
import com.example.hrm.modules.file.repository.FileAttachmentRepository;
import com.example.hrm.modules.contract.repository.SalaryContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
// import com.example.hrm.shared.enums.SalaryContractStatus;
@Service
@RequiredArgsConstructor
public class ContractService {

    private final ContractRepository contractRepository;
    private final EmployeeRepository employeeRepository;
    private final ContractMapper contractMapper;
    private final FileAttachmentRepository fileAttachmentRepository;
    private final FileAttachmentMapper fileAttachmentMapper;
    private final SalaryContractRepository salaryContractRepository;

    /** Tạo Contract + SalaryContract */
    public ContractResponse create(ContractRequest request) {
        var employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, 404));

        var contract = contractMapper.toEntity(request);
        contract.setEmployee(employee);
        contractRepository.save(contract);

        // Tạo SalaryContract từ request.salaryContract
        var salaryReq = request.getSalaryContract();
        var salaryContract = SalaryContract.builder()
                .employee(employee)
                .contract(contract)
                .baseSalary(salaryReq.getBaseSalary())
                .allowance(salaryReq.getAllowance())
                .salaryCoefficient(salaryReq.getSalaryCoefficient())
                .effectiveDate(salaryReq.getEffectiveDate())
                .status(salaryReq.getStatus())
                .build();
        salaryContractRepository.save(salaryContract);

        // Lấy file employee
        var files = fileAttachmentRepository
                .findByRefTypeAndRefIdAndIsDeletedFalse(RefType.EMPLOYEE.getValue(), employee.getId());

        var response = contractMapper.toResponse(contract);
        response.getEmployee().setFileAttachmentResponses(
                files.stream().map(fileAttachmentMapper::toResponse).toList()
        );

        return response;
    }

    /** Update Contract + SalaryContract */
    public ContractResponse update(String id, ContractUpdateRequest request) {
        var contract = contractRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND, 404));

        contractMapper.ContractUpdateRequestToEntity(request, contract);
        contractRepository.save(contract);

        // Update hoặc tạo SalaryContract tương ứng
        var salaryReq = request.getSalaryContract();
        var salaryContract = salaryContractRepository
                .findByEmployee_IdAndContract_IdAndIsDeletedFalse(
                        contract.getEmployee().getId(),
                        contract.getId())
                .orElse(SalaryContract.builder()
                        .employee(contract.getEmployee())
                        .contract(contract)
                        .build());

        salaryContract.setBaseSalary(salaryReq.getBaseSalary());
        salaryContract.setAllowance(salaryReq.getAllowance());
        salaryContract.setSalaryCoefficient(salaryReq.getSalaryCoefficient());
        salaryContract.setEffectiveDate(salaryReq.getEffectiveDate());
        salaryContract.setStatus(salaryReq.getStatus());
        salaryContractRepository.save(salaryContract);

        var files = fileAttachmentRepository
                .findByRefTypeAndRefIdAndIsDeletedFalse(RefType.EMPLOYEE.getValue(), contract.getEmployee().getId());

        var response = contractMapper.toResponse(contract);
        response.getEmployee().setFileAttachmentResponses(
                files.stream().map(fileAttachmentMapper::toResponse).toList()
        );

        return response;
    }

    /** Lấy chi tiết Contract */
    public ContractResponse getById(String id) {
        var contract = contractRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND, 404));

        var fileEmployees = fileAttachmentRepository
                .findByRefTypeAndRefIdAndIsDeletedFalse(RefType.EMPLOYEE.getValue(), contract.getEmployee().getId());
        var fileContracts = fileAttachmentRepository
                .findByRefTypeAndRefIdAndIsDeletedFalse(RefType.CONTRACT.getValue(), contract.getId());

        var response = contractMapper.toResponse(contract);
        response.getEmployee().setFileAttachmentResponses(
                fileEmployees.stream().map(fileAttachmentMapper::toResponse).toList()
        );
        response.setFileAttachmentResponses(
                fileContracts.stream().map(fileAttachmentMapper::toResponse).toList()
        );

        return response;
    }

    /** Danh sách Contract Active */
    public Page<ContractListResponse> getAllContractActive(int page, int size) {
        var listContract = contractRepository
                .findByIsDeletedFalseAndStatus(PageRequest.of(page, size), ContractStatus.ACTIVE);
        return listContract.map(item -> {
            var response = contractMapper.toListResponse(item);
            response.setContractCode(item.getCode());
            response.setContractType(item.getType().name());
            response.setEmployeeCode(item.getEmployee().getCode());
            response.setEmployeeId(item.getEmployee().getId());
            response.setEmployeeName(item.getEmployee().getFirstName());
            return response;
        });
    }

    /** Danh sách Contract Not Active */
    public Page<ContractListResponse> getAllContractNotActive(int page, int size) {
        var listContract = contractRepository
                .findByIsDeletedFalseAndStatusNot(PageRequest.of(page, size), ContractStatus.ACTIVE);
        return listContract.map(item -> {
            var response = contractMapper.toListResponse(item);
            response.setContractCode(item.getCode());
            response.setContractType(item.getType().name());
            response.setEmployeeCode(item.getEmployee().getCode());
            response.setEmployeeId(item.getEmployee().getId());
            response.setEmployeeName(item.getEmployee().getFirstName());
            return response;
        });
    }

    /** Thay đổi trạng thái Contract */
    public ContractResponse changeContractStatus(String id, ContractStatus newStatus) {
        var contract = contractRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND, 404));

        contract.setStatus(newStatus);
        contractRepository.save(contract);

        return contractMapper.toResponse(contract);
    }
}
