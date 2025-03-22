package com.supcarel.spribe.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

import static com.supcarel.spribe.redis.RedisKeyExpirationHandler.BOOKING_KEY_PREFIX;

@Slf4j
@Service
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;
    @Value("${app.booking.expiration-time}")
    private int bookingExpirationTime;

    public RedisService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void createBookingExpiration(UUID bookingId) {
        createBookingExpiration (bookingId, Duration.ofMinutes(bookingExpirationTime));
    }

    public void createBookingExpiration(UUID bookingId, Duration duration) {
        String key = BOOKING_KEY_PREFIX + bookingId;
        redisTemplate.opsForValue().set(key, "b", duration);
    }

    public void cancelBookingExpiration(UUID bookingId) {
        redisTemplate.delete(BOOKING_KEY_PREFIX + bookingId);
    }
}
