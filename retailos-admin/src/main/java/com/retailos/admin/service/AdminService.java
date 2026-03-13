package com.retailos.admin.service;

import com.retailos.common.exception.BusinessException;
import com.retailos.tenant.domain.Tenant;
import com.retailos.tenant.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * AdminService - platform admin operations across all tenants.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final TenantRepository tenantRepository;

    @Transactional(readOnly = true)
    public Page<Tenant> listAllTenants(Pageable pageable) {
        return tenantRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Tenant getTenantById(UUID id) {
        return tenantRepository.findById(id)
                .orElseThrow(() -> new BusinessException("TENANT_NOT_FOUND", "Tenant not found", 404));
    }

    @Transactional
    public Tenant activateTenant(UUID id) {
        Tenant tenant = getTenantById(id);
        tenant.setStatus(Tenant.TenantStatus.ACTIVE);
        log.info("🏪 Tenant {} activated: {}", id, tenant.getName());
        return tenantRepository.save(tenant);
    }

    @Transactional
    public Tenant suspendTenant(UUID id, String reason) {
        Tenant tenant = getTenantById(id);
        tenant.setStatus(Tenant.TenantStatus.SUSPENDED);
        log.warn("🚫 Tenant {} suspended: {} — reason: {}", id, tenant.getName(), reason);
        return tenantRepository.save(tenant);
    }

    @Transactional
    public Tenant createTenantForRetailer(Tenant tenant) {
        if (tenantRepository.existsBySlug(tenant.getSlug())) {
            throw new BusinessException("SLUG_EXISTS", "Tenant slug already taken", 409);
        }
        tenant.setStatus(Tenant.TenantStatus.PENDING_KYC);
        Tenant saved = tenantRepository.save(tenant);
        log.info("🆕 New tenant created: {} ({})", saved.getName(), saved.getSlug());
        // TODO: clone system roles for the new tenant, create owner user
        return saved;
    }

    /**
     * Platform dashboard stats.
     */
    @Transactional(readOnly = true)
    public java.util.Map<String, Object> getPlatformStats() {
        var stats = new java.util.HashMap<String, Object>();
        stats.put("totalTenants", tenantRepository.count());
        // TODO: add more stats — active users, total revenue, etc.
        return stats;
    }
}
