package com.example.hrm.modules.employee.mapper;

import com.example.hrm.modules.employee.dto.request.PositionRequest;
import com.example.hrm.modules.employee.dto.response.PositionResponse;
import com.example.hrm.modules.employee.entity.Position;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PositionMapper {

    PositionResponse toResponse(Position entity);


    @Mapping(target = "id", ignore = true)
    Position toEntity(PositionRequest request);

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(PositionRequest request, @MappingTarget Position entity);
}
