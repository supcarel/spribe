package com.supcarel.spribe.mapper;

import com.supcarel.spribe.model.Payment;
import com.supcarel.spribe.payload.request.PaymentRequest;
import com.supcarel.spribe.payload.response.PaymentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PaymentMapper {
    PaymentMapper MAPPER = Mappers.getMapper(PaymentMapper.class);

    Payment mapRequestToEntity(PaymentRequest paymentRequest);

    PaymentResponse mapEntityToResponse(Payment payment);
}

