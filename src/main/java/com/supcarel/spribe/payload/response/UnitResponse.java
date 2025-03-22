package com.supcarel.spribe.payload.response;

import com.supcarel.spribe.model.UnitType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class UnitResponse {
    private UUID id;
    private UnitType unitType;
    private Integer roomsCount;
    private Integer floor;
    private BigDecimal basePrice;
    private BigDecimal totalPrice;
    private String description;
}
