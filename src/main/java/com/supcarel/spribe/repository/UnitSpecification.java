package com.supcarel.spribe.repository;

import com.supcarel.spribe.model.Booking;
import com.supcarel.spribe.model.Unit;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class UnitSpecification {

    private UnitSpecification() {
    }

    public static Specification<Unit> hasAvailability(boolean isAvailable) {
        return (root, query, cb) -> cb.equal(root.get("isAvailable"), isAvailable);
    }

    public static Specification<Unit> withinPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, cb) -> {
            if (minPrice == null && maxPrice == null) return null;
            if (minPrice != null && maxPrice != null)
                return cb.between(root.get("basePrice"), minPrice, maxPrice);
            else if (minPrice != null)
                return cb.greaterThanOrEqualTo(root.get("basePrice"), minPrice);
            else
                return cb.lessThanOrEqualTo(root.get("basePrice"), maxPrice);
        };
    }

    public static Specification<Unit> hasRoomsCount(Integer roomsCount) {
        return (root, query, cb) -> roomsCount != null ?
                cb.equal(root.get("roomsCount"), roomsCount) : null;
    }

    public static Specification<Unit> hasFloor(Integer floor) {
        return (root, query, cb) -> floor != null ?
                cb.equal(root.get("floor"), floor) : null;
    }

    public static Specification<Unit> hasUnitType(Integer unitTypeId) {
        return (root, query, cb) -> unitTypeId != null ?
                cb.equal(root.get("unitTypeId"), unitTypeId) : null;
    }

    public static Specification<Unit> availableBetween(Instant start, Instant end, List<String> statuses) {
        return (root, query, cb) -> {
            if (start == null || end == null) return null;

            Subquery<UUID> subquery = query.subquery(UUID.class);
            Root<Booking> booking = subquery.from(Booking.class);
            subquery.select(booking.get("unit").get("id"));

            Predicate dateOverlap = cb.and(
                    cb.lessThan(booking.get("startDate"), end),
                    cb.greaterThan(booking.get("endDate"), start)
            );

            Predicate statusPredicate = booking.get("status").in(statuses);

            subquery.where(cb.and(dateOverlap, statusPredicate));
            return cb.not(cb.in(root.get("id")).value(subquery));
        };
    }
}
