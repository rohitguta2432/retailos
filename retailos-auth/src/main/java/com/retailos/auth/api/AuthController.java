package com.retailos.auth.api;

import com.retailos.auth.dto.AuthDtos;
import com.retailos.auth.service.AuthService;
import com.retailos.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Auth REST Controller - handles OTP-based login flow.
 * All endpoints are public (no JWT required).
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "OTP-based login/logout")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/send-otp")
    @Operation(summary = "Send OTP", description = "Sends a 6-digit OTP to the provided phone number")
    public ResponseEntity<ApiResponse<String>> sendOtp(
            @Valid @RequestBody AuthDtos.SendOtpRequest request) {
        authService.sendOtp(request);
        return ResponseEntity.ok(ApiResponse.success("OTP sent successfully"));
    }

    @PostMapping("/verify-otp")
    @Operation(summary = "Verify OTP & Login", description = "Verifies OTP and returns JWT tokens")
    public ResponseEntity<ApiResponse<AuthDtos.TokenResponse>> verifyOtp(
            @Valid @RequestBody AuthDtos.VerifyOtpRequest request) {
        AuthDtos.TokenResponse tokens = authService.verifyOtpAndLogin(request);
        return ResponseEntity.ok(ApiResponse.success(tokens));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh Token", description = "Exchange refresh token for new access token")
    public ResponseEntity<ApiResponse<AuthDtos.TokenResponse>> refreshToken(
            @Valid @RequestBody AuthDtos.RefreshTokenRequest request) {
        AuthDtos.TokenResponse tokens = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success(tokens));
    }
}
