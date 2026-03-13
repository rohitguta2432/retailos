package com.retailos.common.audit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Publishes AuditEvent instances to the Spring event bus.
 * The Audit module listens for these events and persists them.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuditEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public void publish(AuditEvent event) {
        log.debug("Publishing audit event: {} on {} ({})",
                event.getAction(), event.getEntityType(), event.getEntityId());
        eventPublisher.publishEvent(event);
    }
}
