package com.example.hrm.mapper;

import com.example.hrm.dto.response.DepartmentResponse;
import com.example.hrm.entity.Department;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {
    
    @Mapping(target = "subDepartmentResponses", ignore = true)
    DepartmentResponse toResponse(Department department);


}
