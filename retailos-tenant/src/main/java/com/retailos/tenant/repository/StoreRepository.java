package com.retailos.tenant.repository;

import com.retailos.tenant.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StoreRepository extends JpaRepository<Store, UUID> {

    List<Store> findByTenantId(UUID tenantId);

    boolean existsByCodeAndTenantId(String code, UUID tenantId);
}
