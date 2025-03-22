package com.supcarel.spribe.mapper;

import com.supcarel.spribe.model.UnitType;
import com.supcarel.spribe.payload.response.UnitTypeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UnitTypeMapper {
    UnitTypeMapper MAPPER = Mappers.getMapper(UnitTypeMapper.class);

    UnitTypeResponse mapEntityToResponse(UnitType unitType);
}

