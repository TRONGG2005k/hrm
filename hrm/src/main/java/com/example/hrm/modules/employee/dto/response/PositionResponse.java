package com.example.hrm.modules.employee.dto.response;

public record PositionResponse(
        String id,
        String code,
        String name,
        String description,
        Boolean active
) {}
