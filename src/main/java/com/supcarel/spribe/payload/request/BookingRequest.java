package com.supcarel.spribe.payload.request;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class BookingRequest {
    private UUID unitId;
    private Instant startDate;
    private Instant endDate;
}
