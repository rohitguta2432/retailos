package com.retailos.sync.domain;

import com.retailos.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

/**
 * SyncEvent entity - offline-first sync queue.
 * Client sends local changes; server resolves conflicts.
 */
@Entity
@Table(name = "sync_queue")
@Getter
@Setter
@NoArgsConstructor
public class SyncEvent extends BaseEntity {

    @Column(name = "device_id", nullable = false)
    private String deviceId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation", nullable = false)
    private SyncOperation operation;

    @Column(name = "entity_type", nullable = false)
    private String entityType;

    @Column(name = "entity_id")
    private UUID entityId;

    @Column(name = "payload", nullable = false, columnDefinition = "jsonb")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SyncStatus status = SyncStatus.PENDING;

    @Column(name = "client_timestamp", nullable = false)
    private Instant clientTimestamp;

    @Column(name = "server_timestamp")
    private Instant serverTimestamp;

    @Column(name = "conflict_resolution")
    private String conflictResolution;

    @Column(name = "error_message")
    private String errorMessage;

    public enum SyncOperation {
        CREATE, UPDATE, DELETE
    }

    public enum SyncStatus {
        PENDING, PROCESSING, COMPLETED, CONFLICT, FAILED
    }

    public void markCompleted() {
        this.status = SyncStatus.COMPLETED;
        this.serverTimestamp = Instant.now();
    }

    public void markConflict(String resolution) {
        this.status = SyncStatus.CONFLICT;
        this.conflictResolution = resolution;
        this.serverTimestamp = Instant.now();
    }

    public void markFailed(String error) {
        this.status = SyncStatus.FAILED;
        this.errorMessage = error;
        this.serverTimestamp = Instant.now();
    }
}
