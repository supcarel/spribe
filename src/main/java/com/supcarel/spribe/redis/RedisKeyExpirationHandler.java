package com.supcarel.spribe.redis;

import com.supcarel.spribe.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class RedisKeyExpirationHandler {
    public static final String BOOKING_KEY_PREFIX = "booking_expire:";
    private final BookingService bookingService;

    public void handleMessage(String message) {
        try {
            log.info("Received Redis expiration event: {}", message);
            if (message.startsWith(BOOKING_KEY_PREFIX)) {
                String bookingId = message.substring(BOOKING_KEY_PREFIX.length());
                bookingService.processExpiration(UUID.fromString(bookingId));
            } else {
                log.warn("Unknown Redis expiration event: {}", message);
            }
        } catch (Exception e) {
            log.error("Error processing Redis expiration event: {}", e.getMessage());
        }
    }
}
