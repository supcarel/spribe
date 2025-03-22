package com.supcarel.spribe.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "units")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Unit extends DateAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToOne
    @JoinColumn(name = "unit_type_id", nullable = false)
    private UnitType unitType;

    @Column(name = "rooms_count", nullable = false)
    private Integer roomsCount;

    @Column(name = "floor", nullable = false)
    private Integer floor;

    @Column(name = "base_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "description")
    private String description;

    @Column(name = "is_available", nullable = false)
    private boolean available = true;

    /**
     * Вычисляет полную стоимость с учетом 15% наценки системы бронирования
     *
     * @return полная стоимость
     */
    @Transient
    public BigDecimal getTotalPrice() {
        return basePrice.multiply(new BigDecimal("1.15"));
    }
}