package com.portfolio.financecontrol.adapter.out.kafka;

import com.portfolio.financecontrol.application.port.out.EventPublisherPort;
import com.portfolio.financecontrol.domain.event.DomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "false")
public class LoggingEventPublisher implements EventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(LoggingEventPublisher.class);

    @Override
    public void publish(DomainEvent event) {
        log.info("[Evento - Kafka desabilitado] {}: {}", event.eventType(), event);
    }
}
