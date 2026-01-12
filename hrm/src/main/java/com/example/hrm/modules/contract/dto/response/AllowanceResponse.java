package com.example.hrm.modules.contract.dto.response;

import lombok.Data;

@Data
public class AllowanceResponse {
    Long id;
    String code;
    String name;
    String description;
    Boolean active;
}
