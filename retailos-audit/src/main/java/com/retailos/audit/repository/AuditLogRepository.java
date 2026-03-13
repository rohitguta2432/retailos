package com.retailos.audit.repository;

import com.retailos.audit.domain.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

    Page<AuditLog> findByTenantId(UUID tenantId, Pageable pageable);

    Page<AuditLog> findByTenantIdAndEntityType(UUID tenantId, String entityType, Pageable pageable);

    Page<AuditLog> findByEntityTypeAndEntityId(String entityType, UUID entityId, Pageable pageable);

    Page<AuditLog> findByTenantIdAndCreatedAtBetween(UUID tenantId, Instant from, Instant to, Pageable pageable);
}
