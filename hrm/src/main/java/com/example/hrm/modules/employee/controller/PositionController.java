package com.example.hrm.modules.employee.controller;

import com.example.hrm.modules.employee.dto.request.PositionRequest;
import com.example.hrm.modules.employee.dto.response.PositionResponse;
import com.example.hrm.modules.employee.service.PositionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${app.api-prefix}/positions")
@RequiredArgsConstructor
public class PositionController {

    private final PositionService service;

    @PostMapping
    public PositionResponse create(@RequestBody @Valid PositionRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public PositionResponse update(
            @PathVariable String id,
            @RequestBody @Valid PositionRequest request
    ) {
        return service.update(id, request);
    }

    @GetMapping
    public List<PositionResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public PositionResponse getById(@PathVariable String id) {
        return service.getById(id);
    }
}
