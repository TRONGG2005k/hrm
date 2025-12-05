package com.example.hrm.mapper;

import com.example.hrm.dto.request.AddressRequest;
import com.example.hrm.dto.response.AddressResponse;
import com.example.hrm.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "wardId", target = "ward", ignore = true)
    Address toEntity(AddressRequest request);

    @Mapping(source = "ward.id", target = "wardId")
    AddressResponse toResponse(Address entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "wardId", target = "ward", ignore = true)
    void updateEntity(AddressRequest request, @MappingTarget Address entity);
}
