package com.retailos.tenant.service;

import com.retailos.common.exception.BusinessException;
import com.retailos.common.tenant.TenantContext;
import com.retailos.tenant.domain.Store;
import com.retailos.tenant.domain.Tenant;
import com.retailos.tenant.repository.StoreRepository;
import com.retailos.tenant.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * TenantService - manages tenants and their stores.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TenantService {

    private final TenantRepository tenantRepository;
    private final StoreRepository storeRepository;

    @Transactional(readOnly = true)
    public Tenant getCurrentTenant() {
        UUID tenantId = TenantContext.getTenantId();
        return tenantRepository.findById(tenantId)
                .orElseThrow(() -> new BusinessException("TENANT_NOT_FOUND", "Tenant not found", 404));
    }

    @Transactional(readOnly = true)
    public Tenant getTenantById(UUID id) {
        return tenantRepository.findById(id)
                .orElseThrow(() -> new BusinessException("TENANT_NOT_FOUND", "Tenant not found", 404));
    }

    @Transactional
    public Tenant updateTenant(UUID id, Tenant updates) {
        Tenant tenant = getTenantById(id);
        if (updates.getName() != null) tenant.setName(updates.getName());
        if (updates.getPhone() != null) tenant.setPhone(updates.getPhone());
        if (updates.getEmail() != null) tenant.setEmail(updates.getEmail());
        if (updates.getGstNumber() != null) tenant.setGstNumber(updates.getGstNumber());
        if (updates.getAddressLine1() != null) tenant.setAddressLine1(updates.getAddressLine1());
        if (updates.getCity() != null) tenant.setCity(updates.getCity());
        if (updates.getState() != null) tenant.setState(updates.getState());
        if (updates.getPincode() != null) tenant.setPincode(updates.getPincode());
        return tenantRepository.save(tenant);
    }

    // ==================== Store Operations ====================

    @Transactional(readOnly = true)
    public List<Store> getStores() {
        UUID tenantId = TenantContext.getTenantId();
        return storeRepository.findByTenantId(tenantId);
    }

    @Transactional
    public Store createStore(Store store) {
        store.setTenantId(TenantContext.getTenantId());
        if (store.getCode() != null &&
            storeRepository.existsByCodeAndTenantId(store.getCode(), store.getTenantId())) {
            throw new BusinessException("STORE_CODE_EXISTS", "Store code already exists", 409);
        }
        return storeRepository.save(store);
    }

    @Transactional
    public Store updateStore(UUID storeId, Store updates) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException("STORE_NOT_FOUND", "Store not found", 404));

        if (updates.getName() != null) store.setName(updates.getName());
        if (updates.getPhone() != null) store.setPhone(updates.getPhone());
        if (updates.getAddressLine1() != null) store.setAddressLine1(updates.getAddressLine1());
        if (updates.getCity() != null) store.setCity(updates.getCity());
        if (updates.getState() != null) store.setState(updates.getState());
        if (updates.getPincode() != null) store.setPincode(updates.getPincode());

        return storeRepository.save(store);
    }
}
