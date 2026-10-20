package com.portfolio.financecontrol.application.dto;

import com.portfolio.financecontrol.domain.enums.TransactionCategory;
import com.portfolio.financecontrol.domain.enums.TransactionType;
import com.portfolio.financecontrol.domain.model.Transaction;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record TransactionResult(
        Long id,
        Long accountId,
        String accountName,
        TransactionType type,
        TransactionCategory category,
        String description,
        BigDecimal amount,
        LocalDate date,
        Instant createdAt
) {
    public static TransactionResult from(Transaction transaction) {
        return new TransactionResult(
                transaction.getId(),
                transaction.getAccountId(),
                transaction.getAccountName(),
                transaction.getType(),
                transaction.getCategory(),
                transaction.getDescription(),
                transaction.getAmount(),
                transaction.getDate(),
                transaction.getCreatedAt()
        );
    }
}
