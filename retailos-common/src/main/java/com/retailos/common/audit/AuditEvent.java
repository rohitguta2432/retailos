package com.retailos.common.audit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event for audit logging.
 * Published by modules, consumed by the Audit module for persistence.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditEvent {

    private UUID tenantId;
    private UUID userId;
    private String action;           // CREATE, UPDATE, DELETE, LOGIN, IMPERSONATE_START, etc.
    private String entityType;       // product, bill, khata_entry, user, etc.
    private UUID entityId;
    private Object oldValue;         // JSON snapshot before change
    private Object newValue;         // JSON snapshot after change
    private String ipAddress;
    private String userAgent;
    private UUID impersonationSessionId;  // Present if during impersonation
    private Instant timestamp;

    public static AuditEvent create(UUID tenantId, UUID userId, String entityType, UUID entityId, Object newValue) {
        return AuditEvent.builder()
                .tenantId(tenantId)
                .userId(userId)
                .action("CREATE")
                .entityType(entityType)
                .entityId(entityId)
                .newValue(newValue)
                .timestamp(Instant.now())
                .build();
    }

    public static AuditEvent update(UUID tenantId, UUID userId, String entityType, UUID entityId,
                                     Object oldValue, Object newValue) {
        return AuditEvent.builder()
                .tenantId(tenantId)
                .userId(userId)
                .action("UPDATE")
                .entityType(entityType)
                .entityId(entityId)
                .oldValue(oldValue)
                .newValue(newValue)
                .timestamp(Instant.now())
                .build();
    }

    public static AuditEvent of(String action, String entityType, UUID entityId, UUID tenantId, UUID userId) {
        return AuditEvent.builder()
                .tenantId(tenantId)
                .userId(userId)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .timestamp(Instant.now())
                .build();
    }

    public static AuditEvent delete(UUID tenantId, UUID userId, String entityType, UUID entityId, Object oldValue) {
        return AuditEvent.builder()
                .tenantId(tenantId)
                .userId(userId)
                .action("DELETE")
                .entityType(entityType)
                .entityId(entityId)
                .oldValue(oldValue)
                .timestamp(Instant.now())
                .build();
    }
}
