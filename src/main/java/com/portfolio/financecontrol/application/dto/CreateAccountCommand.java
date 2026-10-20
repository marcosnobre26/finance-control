package com.portfolio.financecontrol.application.dto;

import com.portfolio.financecontrol.domain.enums.AccountType;

import java.math.BigDecimal;

public record CreateAccountCommand(
        String name,
        AccountType type,
        BigDecimal initialBalance,
        String currency
) {
}
