package com.supcarel.spribe.service;

import com.supcarel.spribe.exception.BookingAlreadyExistsException;
import com.supcarel.spribe.exception.BookingDoesNotBelongToUserException;
import com.supcarel.spribe.exception.ResourceNotFoundException;
import com.supcarel.spribe.exception.UnitNotAvailableException;
import com.supcarel.spribe.mapper.BookingMapper;
import com.supcarel.spribe.model.Booking;
import com.supcarel.spribe.model.Unit;
import com.supcarel.spribe.model.User;
import com.supcarel.spribe.model.enums.BookingStatusEnum;
import com.supcarel.spribe.payload.request.BookingRequest;
import com.supcarel.spribe.payload.response.BookingResponse;
import com.supcarel.spribe.redis.RedisService;
import com.supcarel.spribe.repository.BookingRepository;
import com.supcarel.spribe.repository.UnitRepository;
import com.supcarel.spribe.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UnitRepository unitRepository;
    private final UserRepository userRepository;
    private final RedisService redisService;
    @Value("${app.booking.expiration-time}")
    private int bookingExpirationTime;

    public BookingService(BookingRepository bookingRepository, UnitRepository unitRepository, UserRepository userRepository, RedisService redisService) {
        this.bookingRepository = bookingRepository;
        this.unitRepository = unitRepository;
        this.userRepository = userRepository;
        this.redisService = redisService;
    }

    @Transactional
    public BookingResponse createBooking(BookingRequest bookingRequest, UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        UUID unitId = bookingRequest.getUnitId();
        Unit unit = unitRepository.findById(unitId).orElseThrow(() -> new ResourceNotFoundException("Unit not found with id: " + unitId));

        // CHeck if unit is available
        if (!unit.isAvailable()) {
            throw new UnitNotAvailableException("Unit is not available for booking");
        }

        // Check overlapping bookings
        if (bookingRepository.countOverlappingBookings(unitId, bookingRequest.getStartDate(), bookingRequest.getEndDate()) > 0) {
            throw new BookingAlreadyExistsException("Unit is already booked for the selected dates");
        }

        // Create booking
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setUnit(unit);
        booking.setStartDate(bookingRequest.getStartDate());
        booking.setEndDate(bookingRequest.getEndDate());
        booking.setTotalPrice(unit.getTotalPrice()); //TODO calculate total price
        booking.setStatus(BookingStatusEnum.PENDING);
        booking.setExpiresAt(Instant.now().plus(Duration.ofMinutes(bookingExpirationTime)));

        Booking savedBooking = bookingRepository.save(booking);

        // Set expiration time for booking
        redisService.createBookingExpiration(savedBooking.getId());

        //TODO eventService.createEvent(...);
        //TODO cacheService.updateCache(...);
        
        return BookingMapper.MAPPER.mapEntityToResponse(savedBooking);
    }

    @Transactional(readOnly = true)
    public Optional<BookingResponse> getBookingById(UUID id) {
        return bookingRepository.findById(id).map(BookingMapper.MAPPER::mapEntityToResponse);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByUserId(UUID userId) {
        return bookingRepository.findByUserId(userId).stream()
                .map(BookingMapper.MAPPER::mapEntityToResponse)
                .toList();
    }

    @Transactional
    public BookingResponse cancelBooking(UUID bookingId, UUID userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        if (!booking.getUser().getId().equals(userId)) {
            throw new BookingDoesNotBelongToUserException("User does not have permission to cancel this booking");
        }

        booking.setStatus(BookingStatusEnum.CANCELLED);
        Booking canceledBooking = bookingRepository.save(booking);

        //TODO cacheService.updateCache(...);
        //TODO eventService.createEvent(...);

        return BookingMapper.MAPPER.mapEntityToResponse(canceledBooking);
    }

    @Transactional
    public void processExpiration(UUID bookingId) {
        bookingRepository.findById(bookingId).ifPresent(booking -> {
            if (booking.getStatus() == BookingStatusEnum.PENDING) {
                booking.setStatus(BookingStatusEnum.EXPIRED);
                bookingRepository.save(booking);

                //TODO cacheService.updateCache(...);
                //TODO eventService.createEvent(...);

                log.info("Booking {} expired via Redis", bookingId);
            }
        });
    }
}
