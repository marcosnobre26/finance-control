package com.portfolio.financecontrol.adapter.out.kafka;

import com.portfolio.financecontrol.application.port.out.EventPublisherPort;
import com.portfolio.financecontrol.domain.event.DomainEvent;
import com.portfolio.financecontrol.domain.event.LowBalanceEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class KafkaEventPublisher implements EventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(KafkaEventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String transactionTopic;
    private final String accountTopic;

    public KafkaEventPublisher(KafkaTemplate<String, Object> kafkaTemplate,
                               @Value("${app.kafka.topics.transaction-events}") String transactionTopic,
                               @Value("${app.kafka.topics.account-events}") String accountTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.transactionTopic = transactionTopic;
        this.accountTopic = accountTopic;
    }

    @Override
    public void publish(DomainEvent event) {
        String topic = resolveTopic(event);
        try {
            kafkaTemplate.send(topic, event).whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Falha ao publicar evento {} no tópico {}: {}", event.eventType(), topic, ex.getMessage());
                } else if (result != null && result.getRecordMetadata() != null) {
                    log.info("Evento {} publicado no tópico {} (offset={})",
                            event.eventType(), topic, result.getRecordMetadata().offset());
                }
            });
        } catch (Exception e) {
            log.warn("Não foi possível publicar o evento {} (não bloqueia o fluxo): {}", event.eventType(), e.getMessage());
        }
    }

    private String resolveTopic(DomainEvent event) {
        if (event instanceof LowBalanceEvent) {
            return accountTopic;
        }
        return transactionTopic;
    }
}
