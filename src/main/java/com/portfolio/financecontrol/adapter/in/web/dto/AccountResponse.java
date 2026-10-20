package com.portfolio.financecontrol.adapter.in.web.dto;

import com.portfolio.financecontrol.application.dto.AccountResult;
import com.portfolio.financecontrol.domain.enums.AccountType;

import java.math.BigDecimal;
import java.time.Instant;

public record AccountResponse(
        Long id,
        String name,
        AccountType type,
        BigDecimal balance,
        String currency,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
    public static AccountResponse from(AccountResult result) {
        return new AccountResponse(
                result.id(),
                result.name(),
                result.type(),
                result.balance(),
                result.currency(),
                result.active(),
                result.createdAt(),
                result.updatedAt()
        );
    }
}
