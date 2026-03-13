package com.retailos.common.tenant;

import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 * ThreadLocal-based tenant context holder.
 * Set by TenantInterceptor from JWT claims on each request.
 * Cleared after request completes.
 */
@Slf4j
public final class TenantContext {

    private static final ThreadLocal<UUID> CURRENT_TENANT = new ThreadLocal<>();

    private TenantContext() {
        // Utility class
    }

    public static void setTenantId(UUID tenantId) {
        log.debug("Setting tenant context: {}", tenantId);
        CURRENT_TENANT.set(tenantId);
    }

    public static UUID getTenantId() {
        return CURRENT_TENANT.get();
    }

    public static UUID requireTenantId() {
        UUID tenantId = CURRENT_TENANT.get();
        if (tenantId == null) {
            throw new IllegalStateException("Tenant context not set. Ensure TenantInterceptor is active.");
        }
        return tenantId;
    }

    public static void clear() {
        log.debug("Clearing tenant context");
        CURRENT_TENANT.remove();
    }
}
