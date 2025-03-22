package com.supcarel.spribe.repository;

import com.supcarel.spribe.model.Unit;
import com.supcarel.spribe.model.enums.BookingStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface UnitRepository extends JpaRepository<Unit, UUID>, JpaSpecificationExecutor<Unit> {
    default Page<Unit> searchUnits(
            boolean isAvailable,
            Instant startDate,
            Instant endDate,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Integer unitTypeId,
            Integer roomsCount,
            Integer floor,
            List<String> statuses,
            Pageable pageable
    ) {
        return findAll(
                Specification.where(UnitSpecification.hasAvailability(isAvailable))
                        .and(UnitSpecification.withinPriceRange(minPrice, maxPrice))
                        .and(UnitSpecification.hasRoomsCount(roomsCount))
                        .and(UnitSpecification.hasFloor(floor))
                        .and(UnitSpecification.hasUnitType(unitTypeId))
                        .and(UnitSpecification.availableBetween(startDate, endDate, statuses)),
                pageable
        );
    }
}
