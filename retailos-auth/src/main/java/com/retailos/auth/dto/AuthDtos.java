package com.retailos.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * Request DTOs for authentication flow.
 */
public final class AuthDtos {

    private AuthDtos() {}

    @Data
    public static class SendOtpRequest {
        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^\\+91[6-9][0-9]{9}$", message = "Invalid Indian phone number")
        private String phone;
    }

    @Data
    public static class VerifyOtpRequest {
        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^\\+91[6-9][0-9]{9}$", message = "Invalid Indian phone number")
        private String phone;

        @NotBlank(message = "OTP is required")
        @Pattern(regexp = "^[0-9]{6}$", message = "OTP must be 6 digits")
        private String otp;
    }

    @Data
    public static class RefreshTokenRequest {
        @NotBlank(message = "Refresh token is required")
        private String refreshToken;
    }

    @Data
    public static class TokenResponse {
        private String accessToken;
        private String refreshToken;
        private String tokenType = "Bearer";
        private long expiresIn;
        private UserInfo user;

        @Data
        public static class UserInfo {
            private String id;
            private String fullName;
            private String phone;
            private String tenantId;
            private java.util.List<String> roles;
        }
    }

    @Data
    public static class RegisterRequest {
        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^\\+91[6-9][0-9]{9}$", message = "Invalid Indian phone number")
        private String phone;

        @NotBlank(message = "Full name is required")
        private String fullName;

        private String email;
        private String tenantId;
    }
}
