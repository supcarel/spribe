package com.supcarel.spribe.repository;


import com.supcarel.spribe.model.Booking;
import com.supcarel.spribe.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findByUserId(UUID userId);

    List<Payment> findByBookingId(UUID bookingID);

    List<Payment> findByBookingAndStatus(Booking booking, String status);
}
