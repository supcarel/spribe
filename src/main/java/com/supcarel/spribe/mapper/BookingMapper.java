package com.supcarel.spribe.mapper;

import com.supcarel.spribe.model.Booking;
import com.supcarel.spribe.model.Unit;
import com.supcarel.spribe.payload.request.BookingRequest;
import com.supcarel.spribe.payload.request.UnitRequest;
import com.supcarel.spribe.payload.response.BookingResponse;
import com.supcarel.spribe.payload.response.UnitResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BookingMapper {
    BookingMapper MAPPER = Mappers.getMapper(BookingMapper.class);

    Booking mapRequestToEntity(BookingRequest bookingRequest);

    BookingResponse mapEntityToResponse(Booking booking);
}

