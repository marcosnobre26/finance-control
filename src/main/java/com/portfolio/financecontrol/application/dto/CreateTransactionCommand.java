package com.portfolio.financecontrol.application.dto;

import com.portfolio.financecontrol.domain.enums.TransactionCategory;
import com.portfolio.financecontrol.domain.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateTransactionCommand(
        Long accountId,
        TransactionType type,
        TransactionCategory category,
        String description,
        BigDecimal amount,
        LocalDate date
) {
}
