package com.example.hrm.mapper;

import com.example.hrm.dto.request.SalaryAdjustmentRequest;
import com.example.hrm.dto.response.SalaryAdjustmentResponse;
import com.example.hrm.entity.SalaryAdjustment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface SalaryAdjustmentMapper {

    SalaryAdjustmentMapper INSTANCE = Mappers.getMapper(SalaryAdjustmentMapper.class);

    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(source = "employeeId", target = "employee.id")
    SalaryAdjustment toEntity(SalaryAdjustmentRequest request);

    @Mapping(source = "employee.id", target = "employeeId")
    SalaryAdjustmentResponse toResponse(SalaryAdjustment entity);
}