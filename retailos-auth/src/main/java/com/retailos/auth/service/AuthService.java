package com.retailos.auth.service;

import com.retailos.auth.domain.User;
import com.retailos.auth.dto.AuthDtos;
import com.retailos.auth.repository.UserRepository;
import com.retailos.common.audit.AuditEvent;
import com.retailos.common.audit.AuditEventPublisher;
import com.retailos.common.exception.BusinessException;
import com.retailos.common.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * AuthService - handles OTP-based authentication flow.
 * Flow: sendOtp() → verifyOtp() → returns JWT tokens.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final OtpService otpService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuditEventPublisher auditPublisher;

    /**
     * Step 1: Send OTP to user's phone number.
     */
    public void sendOtp(AuthDtos.SendOtpRequest request) {
        User user = userRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new BusinessException(
                        "USER_NOT_FOUND",
                        "No account found for phone: " + request.getPhone()
                ));

        if (user.getStatus() != User.UserStatus.ACTIVE) {
            throw new BusinessException("USER_INACTIVE", "Account is not active");
        }

        otpService.generateOtp(request.getPhone());

        auditPublisher.publish(AuditEvent.builder()
                .action("OTP_SENT").entityType("USER")
                .entityId(user.getId()).tenantId(user.getTenantId()).userId(user.getId())
                .build());
    }

    /**
     * Step 2: Verify OTP and return JWT tokens.
     */
    @Transactional
    public AuthDtos.TokenResponse verifyOtpAndLogin(AuthDtos.VerifyOtpRequest request) {
        boolean valid = otpService.verifyOtp(request.getPhone(), request.getOtp());
        if (!valid) {
            throw new BusinessException("INVALID_OTP", "OTP is invalid or expired");
        }

        User user = userRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new BusinessException(
                        "USER_NOT_FOUND", "User not found"
                ));

        user.recordLogin();
        userRepository.save(user);

        List<String> roleNames = user.getRoles().stream()
                .map(r -> r.getName())
                .collect(Collectors.toList());

        String accessToken = jwtTokenProvider.generateAccessToken(
                user.getId(), user.getTenantId(), roleNames
        );
        String refreshToken = jwtTokenProvider.generateRefreshToken(
                user.getId(), user.getTenantId()
        );

        AuthDtos.TokenResponse response = new AuthDtos.TokenResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setExpiresIn(900);

        AuthDtos.TokenResponse.UserInfo userInfo = new AuthDtos.TokenResponse.UserInfo();
        userInfo.setId(user.getId().toString());
        userInfo.setFullName(user.getFullName());
        userInfo.setPhone(user.getPhone());
        userInfo.setTenantId(user.getTenantId().toString());
        userInfo.setRoles(roleNames);
        response.setUser(userInfo);

        auditPublisher.publish(AuditEvent.builder()
                .action("LOGIN_SUCCESS").entityType("USER")
                .entityId(user.getId()).tenantId(user.getTenantId()).userId(user.getId())
                .build());

        log.info("User {} logged in successfully", user.getPhone());
        return response;
    }

    /**
     * Refresh access token using a valid refresh token.
     */
    public AuthDtos.TokenResponse refreshToken(AuthDtos.RefreshTokenRequest request) {
        if (!jwtTokenProvider.validateToken(request.getRefreshToken())) {
            throw new BusinessException("INVALID_TOKEN", "Refresh token is invalid or expired");
        }

        UUID userId = jwtTokenProvider.getUserIdFromToken(request.getRefreshToken());
        UUID tenantId = jwtTokenProvider.getTenantIdFromToken(request.getRefreshToken());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "User not found"));

        List<String> roleNames = user.getRoles().stream()
                .map(r -> r.getName())
                .collect(Collectors.toList());

        String newAccessToken = jwtTokenProvider.generateAccessToken(userId, tenantId, roleNames);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(userId, tenantId);

        AuthDtos.TokenResponse response = new AuthDtos.TokenResponse();
        response.setAccessToken(newAccessToken);
        response.setRefreshToken(newRefreshToken);
        response.setExpiresIn(900);

        return response;
    }
}
