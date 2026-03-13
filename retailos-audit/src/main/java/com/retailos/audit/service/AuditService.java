package com.retailos.audit.service;

import com.retailos.audit.domain.AuditLog;
import com.retailos.audit.repository.AuditLogRepository;
import com.retailos.common.audit.AuditEvent;
import com.retailos.common.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * AuditService - listens for audit events and persists them async.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    /**
     * Event listener: automatically persists AuditEvent to audit_log table.
     */
    @EventListener
    @Async
    public void handleAuditEvent(AuditEvent event) {
        try {
            AuditLog auditLog = new AuditLog();
            auditLog.setTenantId(event.getTenantId());
            auditLog.setAction(event.getAction());
            auditLog.setEntityType(event.getEntityType());
            auditLog.setEntityId(event.getEntityId());
            auditLog.setPerformedBy(event.getUserId());
            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            log.error("Failed to persist audit event: {}", event.getAction(), e);
        }
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> listAuditLogs(Pageable pageable) {
        return auditLogRepository.findByTenantId(TenantContext.getTenantId(), pageable);
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> getEntityAuditTrail(String entityType, UUID entityId, Pageable pageable) {
        return auditLogRepository.findByEntityTypeAndEntityId(entityType, entityId, pageable);
    }
}
