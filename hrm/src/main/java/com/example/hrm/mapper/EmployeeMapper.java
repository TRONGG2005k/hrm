package com.example.hrm.mapper;

import com.example.hrm.dto.request.EmployeeRequest;
import com.example.hrm.dto.response.EmployeeResponse;
import com.example.hrm.entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "avatarUrl", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "contracts", ignore = true)
    @Mapping(target = "subDepartment", ignore = true)
    @Mapping(target = "files", ignore = true)
    Employee toEntity(EmployeeRequest request);

    @Mapping(source = "address.id", target = "addressId")
    @Mapping(source = "subDepartment.id", target = "subDepartmentId")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    EmployeeResponse toResponse(Employee entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "avatarUrl", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "contracts", ignore = true)
    @Mapping(target = "subDepartment", ignore = true)
    @Mapping(target = "files", ignore = true)
    void updateEntity(EmployeeRequest request, @MappingTarget Employee entity);
}