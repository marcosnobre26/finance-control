package com.portfolio.financecontrol.application.dto;

import com.portfolio.financecontrol.domain.enums.AccountType;
import com.portfolio.financecontrol.domain.model.Account;

import java.math.BigDecimal;
import java.time.Instant;

public record AccountResult(
        Long id,
        String name,
        AccountType type,
        BigDecimal balance,
        String currency,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
    public static AccountResult from(Account account) {
        return new AccountResult(
                account.getId(),
                account.getName(),
                account.getType(),
                account.getBalance(),
                account.getCurrency(),
                account.isActive(),
                account.getCreatedAt(),
                account.getUpdatedAt()
        );
    }
}
