package com.supcarel.spribe.payload.request;

import lombok.Data;

import java.util.UUID;

@Data
public class PaymentRequest {
    private UUID bookingId;
}
