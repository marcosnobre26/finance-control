package com.portfolio.financecontrol.adapter.in.web.dto;

import com.portfolio.financecontrol.application.dto.TransactionResult;
import com.portfolio.financecontrol.domain.enums.TransactionCategory;
import com.portfolio.financecontrol.domain.enums.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record TransactionResponse(
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
    public static TransactionResponse from(TransactionResult result) {
        return new TransactionResponse(
                result.id(),
                result.accountId(),
                result.accountName(),
                result.type(),
                result.category(),
                result.description(),
                result.amount(),
                result.date(),
                result.createdAt()
        );
    }
}
