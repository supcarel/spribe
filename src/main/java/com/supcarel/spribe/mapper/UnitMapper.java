package com.supcarel.spribe.mapper;

import com.supcarel.spribe.model.Unit;
import com.supcarel.spribe.payload.request.UnitRequest;
import com.supcarel.spribe.payload.response.UnitResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UnitMapper {
    UnitMapper MAPPER = Mappers.getMapper(UnitMapper.class);

    @Mapping(target = "available", ignore = true)
    Unit mapRequestToEntity(UnitRequest unitRequest);

    UnitResponse mapEntityToResponse(Unit unit);
}

