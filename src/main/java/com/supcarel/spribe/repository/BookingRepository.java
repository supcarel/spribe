package com.supcarel.spribe.repository;

import com.supcarel.spribe.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {

    List<Booking> findByUserId(UUID userId);
    List<Booking> findByUnitId(UUID uuidId);

    List<Booking> findByStatusAndExpiresAtBefore(String status, Instant expiresAt);

    @Query("SELECT COUNT(b) FROM Booking b WHERE " +
            "b.unit.id = :unitId AND " +
            "b.status IN ('PENDING', 'CONFIRMED') AND " +
            "((b.startDate <= :endDate AND b.endDate >= :startDate) OR " +
            "(b.startDate >= :startDate AND b.startDate <= :endDate) OR " +
            "(b.endDate >= :startDate AND b.endDate <= :endDate))")
    long countOverlappingBookings(@Param("unitId") UUID unitId, @Param("startDate") Instant startDate,
                                  @Param("endDate") Instant endDate);
}
