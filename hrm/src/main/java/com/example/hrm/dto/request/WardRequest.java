package com.example.hrm.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import jakarta.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WardRequest {

    @NotBlank(message = "Tên phường/xã không được để trống")
    String name;

    @NotBlank(message = "Mã quận/huyện không được để trống")
    String districtId;
}
