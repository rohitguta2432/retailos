package com.retailos.common.tenant;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Enables the Hibernate tenant filter on the current EntityManager session.
 * Call this in service methods or via an aspect to ensure all queries are tenant-scoped.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TenantFilterActivator {

    private final EntityManager entityManager;

    /**
     * Activates the tenant filter for the current session.
     * Must be called within a transactional context.
     */
    public void activateFilter() {
        UUID tenantId = TenantContext.getTenantId();
        if (tenantId != null) {
            Session session = entityManager.unwrap(Session.class);
            session.enableFilter("tenantFilter").setParameter("tenantId", tenantId);
            log.debug("Tenant filter activated for tenant: {}", tenantId);
        }
    }
}
