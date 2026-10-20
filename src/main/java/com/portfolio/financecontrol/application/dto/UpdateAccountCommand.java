package com.portfolio.financecontrol.application.dto;

import com.portfolio.financecontrol.domain.enums.AccountType;

import java.math.BigDecimal;

public record UpdateAccountCommand(
        String name,
        AccountType type,
        BigDecimal balance,
        String currency
) {
}
