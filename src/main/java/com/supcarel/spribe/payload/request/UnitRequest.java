package com.supcarel.spribe.payload.request;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class UnitRequest {
    private UUID id;
    private Integer unitTypeId;
    private Integer roomsCount;
    private Integer floor;
    private BigDecimal basePrice;
    private String description;
}
