package com.example.hrm.mapper;

import com.example.hrm.dto.response.SubDepartmentResponse;
import com.example.hrm.entity.SubDepartment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubDepartmentMapper {

    // SubDepartmentMapper INSTANCE = Mappers.getMapper(SubDepartmentMapper.class);

    @Mapping(source = "department.id", target = "departmentId")
    SubDepartmentResponse toResponse(SubDepartment subDepartment);
}
