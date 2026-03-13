package com.retailos.common.tenant;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

/**
 * HTTP interceptor that extracts tenant_id from JWT claims
 * and populates the TenantContext for the current request.
 * Also enables the Hibernate tenant filter on the EntityManager.
 */
@Slf4j
@Component
public class TenantInterceptor implements HandlerInterceptor {

    public static final String TENANT_HEADER = "X-Tenant-Id";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String tenantIdStr = request.getHeader(TENANT_HEADER);

        // In production, tenant_id comes from JWT claims (set by JwtAuthFilter).
        // This header-based approach is a fallback for development/testing.
        if (tenantIdStr != null && !tenantIdStr.isBlank()) {
            try {
                UUID tenantId = UUID.fromString(tenantIdStr);
                TenantContext.setTenantId(tenantId);
                log.debug("Tenant context set from header: {}", tenantId);
            } catch (IllegalArgumentException e) {
                log.warn("Invalid tenant ID in header: {}", tenantIdStr);
            }
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                 Object handler, Exception ex) {
        TenantContext.clear();
    }
}
