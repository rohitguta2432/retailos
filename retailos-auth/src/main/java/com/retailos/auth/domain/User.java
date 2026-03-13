package com.retailos.auth.domain;

import com.retailos.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * User entity - a person who can log into the platform.
 * Each user belongs to one tenant (retailer). Platform admins belong to the system tenant.
 * Authentication is Phone + OTP (India-first, no password).
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User extends BaseEntity {

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    public enum UserStatus {
        ACTIVE, INACTIVE, SUSPENDED, PENDING_VERIFICATION
    }

    /**
     * Check if user has a specific role.
     */
    public boolean hasRole(String roleName) {
        return roles.stream().anyMatch(r -> r.getName().equalsIgnoreCase(roleName));
    }

    /**
     * Get all permission strings from all assigned roles.
     */
    public Set<String> getAllPermissions() {
        Set<String> permissions = new HashSet<>();
        roles.forEach(role -> {
            if (role.getPermissions() != null) {
                permissions.addAll(role.getPermissions());
            }
        });
        return permissions;
    }

    /**
     * Record a login event.
     */
    public void recordLogin() {
        this.lastLoginAt = Instant.now();
    }
}
