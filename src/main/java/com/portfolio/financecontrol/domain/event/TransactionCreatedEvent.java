package com.portfolio.financecontrol.domain.event;

import com.portfolio.financecontrol.domain.enums.TransactionCategory;
import com.portfolio.financecontrol.domain.enums.TransactionType;
import com.portfolio.financecontrol.domain.model.Transaction;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record TransactionCreatedEvent(
        String eventType,
        Long transactionId,
        Long accountId,
        String accountName,
        TransactionType type,
        TransactionCategory category,
        BigDecimal amount,
        LocalDate date,
        Instant occurredAt
) implements DomainEvent {

    public static TransactionCreatedEvent from(Transaction transaction) {
        return new TransactionCreatedEvent(
                "TRANSACTION_CREATED",
                transaction.getId(),
                transaction.getAccountId(),
                transaction.getAccountName(),
                transaction.getType(),
                transaction.getCategory(),
                transaction.getAmount(),
                transaction.getDate(),
                Instant.now()
        );
    }
}
