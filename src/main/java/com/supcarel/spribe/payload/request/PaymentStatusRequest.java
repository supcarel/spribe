package com.supcarel.spribe.payload.request;

import lombok.Data;

import java.util.UUID;

@Data
public class PaymentStatusRequest {
    private UUID paymentId;
//    private String status;
}
