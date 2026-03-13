package com.retailos.common.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;

/**
 * JWT token provider for creating and validating access/refresh tokens.
 * Embeds: sub (userId), tenant_id, roles[], impersonation flag.
 */
@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long accessTokenExpirationMs;
    private final long refreshTokenExpirationMs;

    public JwtTokenProvider(
            @Value("${retailos.security.jwt.secret}") String jwtSecret,
            @Value("${retailos.security.jwt.access-expiration-ms:900000}") long accessTokenExpirationMs,
            @Value("${retailos.security.jwt.refresh-expiration-ms:604800000}") long refreshTokenExpirationMs) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
        this.accessTokenExpirationMs = accessTokenExpirationMs;   // 15 min default
        this.refreshTokenExpirationMs = refreshTokenExpirationMs; // 7 days default
    }

    public String generateAccessToken(UUID userId, UUID tenantId, List<String> roles) {
        return generateToken(userId, tenantId, roles, accessTokenExpirationMs, "access");
    }

    public String generateRefreshToken(UUID userId, UUID tenantId) {
        return generateToken(userId, tenantId, List.of(), refreshTokenExpirationMs, "refresh");
    }

    private String generateToken(UUID userId, UUID tenantId, List<String> roles,
                                  long expirationMs, String tokenType) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(userId.toString())
                .claim("tenant_id", tenantId.toString())
                .claim("roles", roles)
                .claim("token_type", tokenType)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    public UUID getUserIdFromToken(String token) {
        return UUID.fromString(parseToken(token).getSubject());
    }

    public UUID getTenantIdFromToken(String token) {
        return UUID.fromString(parseToken(token).get("tenant_id", String.class));
    }

    @SuppressWarnings("unchecked")
    public List<String> getRolesFromToken(String token) {
        return parseToken(token).get("roles", List.class);
    }
}
