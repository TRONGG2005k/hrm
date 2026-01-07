package com.example.hrm.modules.contract.controller;

import com.example.hrm.modules.contract.dto.request.ContractRequest;
import com.example.hrm.modules.contract.dto.request.ContractUpdateRequest;
import com.example.hrm.modules.contract.dto.response.ContractListResponse;
import com.example.hrm.modules.contract.dto.response.ContractResponse;
import com.example.hrm.shared.enums.ContractStatus;
import com.example.hrm.modules.contract.service.ContractService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${app.api-prefix}/contract")
public class ContractController {
    private final ContractService contractService;

    @PostMapping
    public ResponseEntity<ContractResponse> create(@Valid @RequestBody ContractRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(contractService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContractResponse> update(
            @PathVariable String id,
            @Valid @RequestBody ContractUpdateRequest request
    ) {
        return ResponseEntity.ok(contractService.update(id, request));
    }

    @GetMapping
    public ResponseEntity<Page<ContractListResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    )
    {
        return ResponseEntity.ok(contractService.getAllContractActive(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContractResponse> getContractById(
            @PathVariable String id
    ) {
        return ResponseEntity.ok(contractService.getById(id));
    }

    public Page<ContractListResponse> getContractsNotActive(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return contractService.getAllContractNotActive(page, size);
    }

    @RestController
    @RequestMapping("/api/contracts")
    @RequiredArgsConstructor
    public class ContractApprovalController {

        private final ContractService contractService;

        /**
         * Duyệt hợp đồng
         * @param contractId id hợp đồng
         * @param newStatus trạng thái mới (vd: ACTIVE, TERMINATED)
         */
        @PostMapping("/{contractId}/approve")
        public ContractResponse approveContract(
                @PathVariable String contractId,
                @RequestParam ContractStatus newStatus
        ) {
            return contractService.changeContractStatus(contractId, newStatus);
        }
    }

}
