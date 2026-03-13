package com.retailos.common.security;

import com.retailos.common.tenant.TenantContext;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Utility class for accessing the current authenticated user and tenant context.
 */
public final class SecurityUtils {

    private SecurityUtils() {}

    public static UUID getCurrentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal principal) {
            return principal.getUserId();
        }
        throw new IllegalStateException("No authenticated user found in security context");
    }

    public static UUID getCurrentTenantId() {
        return TenantContext.requireTenantId();
    }

    public static List<String> getCurrentRoles() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            return auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    public static boolean hasRole(String role) {
        return getCurrentRoles().contains("ROLE_" + role);
    }

    /**
     * Custom UserPrincipal for Spring Security context.
     */
    @Getter
    public static class UserPrincipal {
        private final UUID userId;
        private final UUID tenantId;
        private final List<String> roles;

        public UserPrincipal(UUID userId, UUID tenantId, List<String> roles) {
            this.userId = userId;
            this.tenantId = tenantId;
            this.roles = roles;
        }

        public Collection<? extends GrantedAuthority> getAuthorities() {
            return roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.toList());
        }
    }
}
