package com.retailos.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;

/**
 * OTP Service - generates 6-digit OTP and stores in Redis with TTL.
 * MVP uses mock OTP (logs to console). Production: integrate MSG91/Twilio.
 */
@Service
@Slf4j
public class OtpService {

    private static final int OTP_LENGTH = 6;
    private static final Duration OTP_TTL = Duration.ofMinutes(5);
    private static final String OTP_PREFIX = "otp:";
    private static final SecureRandom random = new SecureRandom();

    private final StringRedisTemplate redisTemplate;

    public OtpService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Generate and store a 6-digit OTP for the given phone number.
     * In MVP, also logs the OTP for testing.
     */
    public String generateOtp(String phone) {
        String otp = generateSecureOtp();

        // Store in Redis with 5-minute TTL
        redisTemplate.opsForValue().set(OTP_PREFIX + phone, otp, OTP_TTL);

        // MVP: log OTP for development testing
        log.info("📱 OTP for {}: {} (expires in 5 min)", phone, otp);

        // TODO: Production - call SMS gateway (MSG91, Twilio, etc.)
        // smsService.sendOtp(phone, otp);

        return otp;
    }

    /**
     * Verify OTP for the given phone number.
     * Deletes OTP after successful verification (one-time use).
     */
    public boolean verifyOtp(String phone, String otp) {
        String storedOtp = redisTemplate.opsForValue().get(OTP_PREFIX + phone);

        if (storedOtp == null) {
            log.warn("OTP expired or not found for phone: {}", phone);
            return false;
        }

        if (storedOtp.equals(otp)) {
            // Delete OTP after successful verification
            redisTemplate.delete(OTP_PREFIX + phone);
            return true;
        }

        log.warn("Invalid OTP attempt for phone: {}", phone);
        return false;
    }

    /**
     * Generate a cryptographically secure 6-digit OTP.
     */
    private String generateSecureOtp() {
        int otp = random.nextInt(900000) + 100000; // 100000-999999
        return String.valueOf(otp);
    }
}
