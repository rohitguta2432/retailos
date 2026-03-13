package com.retailos.sync.repository;

import com.retailos.sync.domain.SyncEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SyncEventRepository extends JpaRepository<SyncEvent, UUID> {

    List<SyncEvent> findByDeviceIdAndTenantIdAndStatus(String deviceId, UUID tenantId, SyncEvent.SyncStatus status);

    Page<SyncEvent> findByTenantIdOrderByClientTimestampDesc(UUID tenantId, Pageable pageable);

    long countByTenantIdAndStatus(UUID tenantId, SyncEvent.SyncStatus status);
}
