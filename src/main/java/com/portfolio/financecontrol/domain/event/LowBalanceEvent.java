package com.portfolio.financecontrol.domain.event;

import com.portfolio.financecontrol.domain.model.Account;

import java.math.BigDecimal;
import java.time.Instant;

public record LowBalanceEvent(
        String eventType,
        Long accountId,
        String accountName,
        BigDecimal balance,
        Instant occurredAt
) implements DomainEvent {

    public static LowBalanceEvent from(Account account) {
        return new LowBalanceEvent(
                "LOW_BALANCE",
                account.getId(),
                account.getName(),
                account.getBalance(),
                Instant.now()
        );
    }
}
