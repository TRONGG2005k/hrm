package com.example.hrm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DistrictResponse {

    String id;

    String name;

    String provinceId;

    LocalDateTime createdAt;

    LocalDateTime updatedAt;

    Boolean isDeleted;

    LocalDateTime deletedAt;
}
