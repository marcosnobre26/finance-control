package com.portfolio.financecontrol.adapter.in.kafka;

import com.portfolio.financecontrol.domain.event.LowBalanceEvent;
import com.portfolio.financecontrol.domain.event.TransactionCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class FinanceEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(FinanceEventConsumer.class);

    @KafkaListener(topics = "${app.kafka.topics.transaction-events}", groupId = "${spring.kafka.consumer.group-id}")
    public void onTransactionCreated(TransactionCreatedEvent event) {
        log.info("[Kafka] Notificação disparada: {} de {} na conta {} ({})",
                event.type(), event.amount(), event.accountName(), event.date());
        // Ponto de extensão: enviar e-mail/SMS/push ou publicar em outro tópico.
    }

    @KafkaListener(topics = "${app.kafka.topics.account-events}", groupId = "${spring.kafka.consumer.group-id}")
    public void onLowBalance(LowBalanceEvent event) {
        log.info("[Kafka] Alerta de saldo baixo: conta {} com saldo {}", event.accountName(), event.balance());
        // Ponto de extensão: disparar alerta/notificação de saldo insuficiente.
    }
}
