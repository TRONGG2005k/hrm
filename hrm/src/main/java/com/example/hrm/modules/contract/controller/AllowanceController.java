package com.example.hrm.modules.contract.controller;

import com.example.hrm.modules.contract.dto.request.AllowanceRequest;
import com.example.hrm.modules.contract.dto.response.AllowanceResponse;
import com.example.hrm.modules.contract.service.AllowanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${app.api-prefix}/allowances")
@RequiredArgsConstructor
public class AllowanceController {

    private final AllowanceService service;

    @PostMapping
    public AllowanceResponse create(
            @RequestBody @Valid AllowanceRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<AllowanceResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public AllowanceResponse getById(@PathVariable String id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public AllowanceResponse update(
            @PathVariable String id,
            @RequestBody @Valid AllowanceRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}
