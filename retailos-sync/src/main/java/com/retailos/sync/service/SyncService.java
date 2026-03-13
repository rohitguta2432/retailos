package com.retailos.sync.service;

import com.retailos.common.security.SecurityUtils;
import com.retailos.common.tenant.TenantContext;
import com.retailos.sync.domain.SyncEvent;
import com.retailos.sync.repository.SyncEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SyncService {

    private final SyncEventRepository syncRepository;

    /**
     * Push a batch of offline changes from the client device.
     * Returns processed events with status (COMPLETED/CONFLICT/FAILED).
     */
    @Transactional
    public List<SyncEvent> pushChanges(List<SyncEvent> events) {
        var tenantId = TenantContext.getTenantId();
        var userId = SecurityUtils.getCurrentUserId();

        for (SyncEvent event : events) {
            event.setTenantId(tenantId);
            event.setUserId(userId);
            try {
                // TODO: Apply change to the target entity (product, bill, etc.)
                // For now, just mark as completed
                event.markCompleted();
                log.debug("Sync event {} completed: {} on {}", event.getOperation(),
                        event.getEntityType(), event.getEntityId());
            } catch (Exception e) {
                event.markFailed(e.getMessage());
                log.warn("Sync event failed: {}", e.getMessage());
            }
        }
        return syncRepository.saveAll(events);
    }

    /**
     * Get pending changes that need to be pulled to a specific device.
     */
    @Transactional(readOnly = true)
    public List<SyncEvent> getPendingForDevice(String deviceId) {
        return syncRepository.findByDeviceIdAndTenantIdAndStatus(
                deviceId, TenantContext.getTenantId(), SyncEvent.SyncStatus.PENDING);
    }

    @Transactional(readOnly = true)
    public Page<SyncEvent> getSyncHistory(Pageable pageable) {
        return syncRepository.findByTenantIdOrderByClientTimestampDesc(
                TenantContext.getTenantId(), pageable);
    }
}
