package com.retailos.notification.service;

import com.retailos.common.security.SecurityUtils;
import com.retailos.common.tenant.TenantContext;
import com.retailos.notification.domain.Notification;
import com.retailos.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public Notification send(UUID userId, String title, String body,
                              Notification.Channel channel, String actionUrl,
                              String entityType, UUID entityId) {
        Notification n = new Notification();
        n.setTenantId(TenantContext.getTenantId());
        n.setUserId(userId);
        n.setTitle(title);
        n.setBody(body);
        n.setChannel(channel);
        n.setActionUrl(actionUrl);
        n.setEntityType(entityType);
        n.setEntityId(entityId);
        n.setStatus(Notification.NotificationStatus.SENT);

        // TODO: for SMS/EMAIL/PUSH, integrate gateway here
        if (channel == Notification.Channel.SMS) {
            log.info("📲 SMS to user {}: {}", userId, title);
        }

        return notificationRepository.save(n);
    }

    @Transactional(readOnly = true)
    public Page<Notification> getMyNotifications(Pageable pageable) {
        return notificationRepository.findByUserIdAndTenantIdOrderByCreatedAtDesc(
                SecurityUtils.getCurrentUserId(), TenantContext.getTenantId(), pageable);
    }

    @Transactional(readOnly = true)
    public long getUnreadCount() {
        return notificationRepository.countByUserIdAndTenantIdAndStatus(
                SecurityUtils.getCurrentUserId(), TenantContext.getTenantId(),
                Notification.NotificationStatus.SENT);
    }

    @Transactional
    public int markAllAsRead() {
        return notificationRepository.markAllAsRead(
                SecurityUtils.getCurrentUserId(), TenantContext.getTenantId());
    }
}
