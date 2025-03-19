package com.supcarel.spribe.service;

import com.supcarel.spribe.exception.PaymentException;
import com.supcarel.spribe.exception.ResourceNotFoundException;
import com.supcarel.spribe.model.Booking;
import com.supcarel.spribe.model.Payment;
import com.supcarel.spribe.model.User;
import com.supcarel.spribe.model.enums.BookingStatusEnum;
import com.supcarel.spribe.model.enums.PaymentStatusEnum;
import com.supcarel.spribe.repository.BookingRepository;
import com.supcarel.spribe.repository.PaymentRepository;
import com.supcarel.spribe.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public PaymentService(
            PaymentRepository paymentRepository,
            BookingRepository bookingRepository,
            UserRepository userRepository) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }


    public Payment createPayment(UUID bookingId, UUID userId, BigDecimal amount) {
        // Проверяем существование бронирования
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        // Проверяем существование пользователя
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Проверяем соответствие пользователя и бронирования
        if (!booking.getUser().getId().equals(userId)) {
            throw new PaymentException("User does not have permission to pay for this booking");
        }

        // Проверяем статус бронирования
        if (!booking.getStatus().equals("PENDING")) {
            throw new PaymentException("Cannot create payment for booking with status: " + booking.getStatus());
        }

        // Создаем платеж
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setUser(user);
        payment.setAmount(amount);
        payment.setStatus(PaymentStatusEnum.PENDING);
        payment.setCreatedAt(Instant.now());

        Payment savedPayment = paymentRepository.save(payment);

        // Создаем событие о создании платежа
        // eventService.createEvent(...);

        return savedPayment;
    }


    @Transactional(readOnly = true)
    public Optional<Payment> getPaymentById(UUID id) {
        return paymentRepository.findById(id);
    }


    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByBookingId(UUID bookingId) {
        return paymentRepository.findByBookingId(bookingId);
    }


    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByUserId(UUID userId) {
        return paymentRepository.findByUserId(userId);
    }

    public Payment processPayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));

        if (!payment.getStatus().equals(PaymentStatusEnum.PENDING)) {
            throw new PaymentException("Payment is not in PENDING status");
        }

        // Здесь была бы интеграция с платежной системой
        // Эмулируем успешную оплату
        payment.setStatus(PaymentStatusEnum.PAID);
        Payment updatedPayment = paymentRepository.save(payment);

        // Обновляем статус бронирования
        Booking booking = payment.getBooking();
        booking.setStatus(BookingStatusEnum.CONFIRMED);
        booking.setUpdatedAt(Instant.now());
        bookingRepository.save(booking);

        // Создаем события
        // eventService.createEvent(...); // Событие об успешной оплате
        // eventService.createEvent(...); // Событие о подтверждении бронирования

        return updatedPayment;
    }

    public Payment cancelPayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));

        if (!payment.getStatus().equals(PaymentStatusEnum.PENDING)) {
            throw new PaymentException("Only PENDING payments can be cancelled");
        }

        payment.setStatus(PaymentStatusEnum.CANCELLED);
        Payment updatedPayment = paymentRepository.save(payment);

        // Создаем событие об отмене платежа
        // eventService.createEvent(...);

        return updatedPayment;
    }
}
