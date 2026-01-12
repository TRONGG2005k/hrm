package com.example.hrm.modules.employee.dto.request;

public record PositionRequest(
        String code,
        String name,
        String description,
        Boolean active
) {}
