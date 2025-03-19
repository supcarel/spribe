package com.supcarel.spribe.service;

import com.supcarel.spribe.exception.BookingAlreadyExistsException;
import com.supcarel.spribe.exception.ResourceNotFoundException;
import com.supcarel.spribe.model.Booking;
import com.supcarel.spribe.model.Unit;
import com.supcarel.spribe.model.User;
import com.supcarel.spribe.model.enums.BookingStatusEnum;
import com.supcarel.spribe.repository.BookingRepository;
import com.supcarel.spribe.repository.UnitRepository;
import com.supcarel.spribe.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UnitRepository unitRepository;
    private final UserRepository userRepository;

    private final CacheService cacheService;

    public BookingService(BookingRepository bookingRepository, UnitRepository unitRepository, UserRepository userRepository, CacheService cacheService) {
        this.bookingRepository = bookingRepository;
        this.unitRepository = unitRepository;
        this.userRepository = userRepository;
        this.cacheService = cacheService;
    }

    public Booking createBooking(UUID userId, UUID unitId, Instant startDate, Instant endDate) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Unit unit = unitRepository.findById(unitId).orElseThrow(() -> new ResourceNotFoundException("Unit not found with id: " + unitId));

        // CHeck if unit is available
        if (!unit.isAvailable()) {
            throw new BookingAlreadyExistsException("Unit is not available for booking");
        }

        // Check overlapping bookings
        if (bookingRepository.countOverlappingBookings(unitId, startDate, endDate) > 0) {
            throw new BookingAlreadyExistsException("Unit is already booked for the selected dates");
        }

        // Create booking
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setUnit(unit);
        booking.setStartDate(startDate);
        booking.setEndDate(endDate);
        booking.setTotalPrice(unit.getTotalPrice()); //TODO calculate total price
        booking.setStatus(BookingStatusEnum.PENDING);
        booking.setExpiresAt(Instant.now().plus(Duration.ofMinutes(15))); //TODO add to properties
        booking.setCreatedAt(Instant.now());
        booking.setUpdatedAt(Instant.now());

        Booking savedBooking = bookingRepository.save(booking);

        // Update cache
        cacheService.refreshCache();

        return savedBooking;
    }

    @Transactional(readOnly = true)
    public Optional<Booking> getBookingById(UUID id) {
        return bookingRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Booking> getBookingsByUserId(UUID userId) {
        return bookingRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Booking> getBookingsByUnitId(UUID unitId) {
        return bookingRepository.findByUnitId(unitId);
    }

    public void cancelBooking(UUID bookingId, UUID userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        if (!booking.getUser().getId().equals(userId)) {
            throw new BookingAlreadyExistsException("User does not have permission to cancel this booking");
        }

        booking.setStatus(BookingStatusEnum.CANCELLED);
        bookingRepository.save(booking);

        // Update cache
        cacheService.refreshCache();
    }

    public void expireBookings() {
        List<Booking> expiredBookings = bookingRepository.findByStatusAndExpiresAtBefore(BookingStatusEnum.PENDING.name(), Instant.now());

        for (Booking booking : expiredBookings) {
            booking.setStatus(BookingStatusEnum.EXPIRED);
            booking.setUpdatedAt(Instant.now());
            bookingRepository.save(booking);

            // Создаем событие об истечении срока бронирования
            // eventService.createEvent(...);
        }

        if (!expiredBookings.isEmpty()) {
            // Update cache
            cacheService.refreshCache();
        }
    }
}
