package com.supcarel.spribe.service;

import com.supcarel.spribe.exception.PaymentException;
import com.supcarel.spribe.exception.PaymentStatusException;
import com.supcarel.spribe.exception.ResourceNotFoundException;
import com.supcarel.spribe.mapper.PaymentMapper;
import com.supcarel.spribe.model.Booking;
import com.supcarel.spribe.model.Payment;
import com.supcarel.spribe.model.User;
import com.supcarel.spribe.model.enums.BookingStatusEnum;
import com.supcarel.spribe.model.enums.PaymentStatusEnum;
import com.supcarel.spribe.payload.request.PaymentRequest;
import com.supcarel.spribe.payload.request.PaymentStatusRequest;
import com.supcarel.spribe.payload.response.PaymentResponse;
import com.supcarel.spribe.redis.RedisService;
import com.supcarel.spribe.repository.BookingRepository;
import com.supcarel.spribe.repository.PaymentRepository;
import com.supcarel.spribe.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final RedisService redisService;

    public PaymentService(
            PaymentRepository paymentRepository,
            BookingRepository bookingRepository,
            UserRepository userRepository, RedisService redisService) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.redisService = redisService;
    }

    @Transactional
    public PaymentResponse createPayment(PaymentRequest paymentRequest, UUID userId) {
        Booking booking = bookingRepository.findById(paymentRequest.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + paymentRequest.getBookingId()));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (!booking.getUser().getId().equals(userId)) {
            log.error("User does not have permission to pay for this booking. BookingId={}, UserId={}", paymentRequest.getBookingId(), userId);
            throw new PaymentException("User does not have permission to pay for this booking");
        }

        if (!BookingStatusEnum.PENDING.equals(booking.getStatus())) {
            log.error("Cannot create payment {} for booking with status: {}", booking.getId(), booking.getStatus());
            throw new PaymentStatusException("Cannot create payment for booking with status: " + booking.getStatus());
        }

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setUser(user);
        payment.setAmount(booking.getTotalPrice());
        payment.setStatus(PaymentStatusEnum.PENDING);

        Payment savedPayment = paymentRepository.save(payment);

        //TODO cacheService.updateCache(...);
        //TODO eventService.createEvent(...);

        return PaymentMapper.MAPPER.mapEntityToResponse(savedPayment);
    }

    @Transactional(readOnly = true)
    public Optional<Payment> getPaymentById(UUID id) {
        return paymentRepository.findById(id);
    }

    @Transactional
    public void processPayment(PaymentStatusRequest paymentStatus) {
        Payment payment = paymentRepository.findById(paymentStatus.getPaymentId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentStatus.getPaymentId()));

        if (!PaymentStatusEnum.PENDING.equals(payment.getStatus())) {
            throw new PaymentException("Payment is not in PENDING status");
        }

        payment.setStatus(PaymentStatusEnum.PAID);
        paymentRepository.save(payment);

        // Обновляем статус бронирования
        Booking booking = payment.getBooking();
        booking.setStatus(BookingStatusEnum.CONFIRMED);
        booking.setUpdatedAt(Instant.now());
        bookingRepository.save(booking);

        redisService.cancelBookingExpiration(booking.getId());

        //TODO eventService.createEvent(...); // Событие об успешной оплате
        //TODO eventService.createEvent(...); // Событие о подтверждении бронирования
    }

    @Transactional
    public Payment cancelPayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));

        if (!PaymentStatusEnum.PENDING.equals(payment.getStatus())) {
            throw new PaymentException("Only PENDING payments can be cancelled");
        }

        payment.setStatus(PaymentStatusEnum.CANCELLED);
        Payment updatedPayment = paymentRepository.save(payment);

        Booking booking = payment.getBooking();
        booking.setStatus(BookingStatusEnum.CANCELLED);
        booking.setUpdatedAt(Instant.now());
        bookingRepository.save(booking);

        //TODO cacheService.updateCache(...);
        //TODO eventService.createEvent(...) оздаем событие об отмене платежа;
        //TODO eventService.createEvent(...); // Событие о отмене бронирования

        return updatedPayment;
    }
}
