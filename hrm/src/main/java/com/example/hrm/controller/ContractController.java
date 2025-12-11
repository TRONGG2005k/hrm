package com.example.hrm.controller;

import com.example.hrm.dto.request.ContractRequest;
import com.example.hrm.dto.request.ContractUpdateRequest;
import com.example.hrm.dto.response.ContractListResponse;
import com.example.hrm.dto.response.ContractResponse;
import com.example.hrm.service.ContractService;
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
        return ResponseEntity.ok(contractService.getAll(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContractResponse> getContractById(
            @PathVariable String id
    ) {
        return ResponseEntity.ok(contractService.getById(id));
    }

}
