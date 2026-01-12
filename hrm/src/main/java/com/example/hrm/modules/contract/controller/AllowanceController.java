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
}
