package com.example.hrm.modules.employee.dto.response;

public record PositionResponse(
        Long id,
        String code,
        String name,
        String description,
        Boolean active
) {}
