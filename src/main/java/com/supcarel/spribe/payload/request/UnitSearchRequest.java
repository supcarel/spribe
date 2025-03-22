package com.supcarel.spribe.payload.request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class UnitSearchRequest {
    private Instant startDate;
    private Instant endDate;
    private Integer unitTypeId;
    private Integer roomsCount;
    private Integer floor;
    private BigDecimal basePriceFrom;
    private BigDecimal basePriceTo;
    private String description;
}
