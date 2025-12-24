package com.example.hrm.service;

import com.example.hrm.dto.request.ContractRequest;
import com.example.hrm.dto.request.ContractUpdateRequest;
import com.example.hrm.dto.response.ContractListResponse;
import com.example.hrm.dto.response.ContractResponse;
import com.example.hrm.enums.ContractStatus;
import com.example.hrm.enums.RefType;
import com.example.hrm.exception.AppException;
import com.example.hrm.exception.ErrorCode;
import com.example.hrm.mapper.ContractMapper;
import com.example.hrm.mapper.FileAttachmentMapper;
import com.example.hrm.repository.ContractRepository;
import com.example.hrm.repository.EmployeeRepository;
import com.example.hrm.repository.FileAttachmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContractService {
    private final ContractRepository contractRepository;
    private final EmployeeRepository employeeRepository;
    private final ContractMapper contractMapper;
    private final FileAttachmentRepository fileAttachmentRepository;
    private final FileAttachmentMapper fileAttachmentMapper;

    /*
    * tạo và trả ra thông tin chi tiết
    * */
    public ContractResponse create(ContractRequest request){
        var contract = contractMapper.toEntity(request);
        var employee = employeeRepository.findEmployeeWithFiles(request.getEmployeeId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, 404));
        var files = fileAttachmentRepository
                .findByRefTypeAndRefIdAndIsDeletedFalse(
                        RefType.EMPLOYEE.getValue(), employee.getId());
        contract.setEmployee(employee);
        contractRepository.save(contract);
        var response = contractMapper.toResponse(contract);
        response.getEmployee().setFileAttachmentResponses(
                files.stream().map(
                        fileAttachmentMapper::toResponse).toList());
        return response;
    }

    /*
     * cập nhật và trả ra thông tin chi tiết
     * */
    public ContractResponse update(String id, ContractUpdateRequest request){
        var contract = contractRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND, 404));
        contractMapper.ContractUpdateRequestToEntity(request, contract);
        contractRepository.save(contract);
        var response = contractMapper.toResponse(contract);
        var files = fileAttachmentRepository
                .findByRefTypeAndRefIdAndIsDeletedFalse(
                        RefType.EMPLOYEE.getValue(), contract.getEmployee().getId());
        response.getEmployee().setFileAttachmentResponses(
                files.stream().map(
                        fileAttachmentMapper::toResponse).toList());
        return response;
    }

    /*
     * lấy ra thông tin chi tiết của 1 contract
     * */
    public ContractResponse getById(String id){
        var contract = contractRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND, 404));
        contractRepository.save(contract);
        var response = contractMapper.toResponse(contract);
        var fileEmployees = fileAttachmentRepository
                .findByRefTypeAndRefIdAndIsDeletedFalse(
                        RefType.EMPLOYEE.getValue(), contract.getEmployee().getId());
        var fileContracts = fileAttachmentRepository
                .findByRefTypeAndRefIdAndIsDeletedFalse(
                        RefType.CONTRACT.getValue(), contract.getId());
        response.getEmployee().setFileAttachmentResponses(
                fileEmployees.stream().map(
                        fileAttachmentMapper::toResponse).toList());
        response.setFileAttachmentResponses(
                fileContracts.stream().map(
                        fileAttachmentMapper::toResponse).toList());
        return response;
    }

    public Page<ContractListResponse> getAllContractActive(int page, int size){
        var listContract = contractRepository
                .findByIsDeletedFalseAndStatus(PageRequest.of(page, size), ContractStatus.ACTIVE);
        return listContract.map(item ->{
            var response = contractMapper.toListResponse(item);
            response.setContractCode(item.getCode());
            response.setContractType(item.getType().name());
            response.setEmployeeCode(item.getEmployee().getCode());
            response.setEmployeeId(item.getEmployee().getId());
            response.setEmployeeName(item.getEmployee().getFirstName());
            return response;
        });
    }

    public Page<ContractListResponse> getAllContractNotActive(int page, int size){
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

    public ContractResponse changeContractStatus(String id, ContractStatus newStatus){
        var contract = contractRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND, 404));

        // Lưu lịch sử trước khi thay đổi trạng thái
        // saveContractHistory(contract, newStatus);

        contract.setStatus(newStatus);
        contractRepository.save(contract);
        return contractMapper.toResponse(contract);
    }

}
