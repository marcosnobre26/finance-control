package com.portfolio.financecontrol.adapter.in.web.dto;

import com.portfolio.financecontrol.application.dto.CreateAccountCommand;
import com.portfolio.financecontrol.application.dto.UpdateAccountCommand;
import com.portfolio.financecontrol.domain.enums.AccountType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record AccountRequest(
        @NotBlank(message = "name is required")
        @Size(max = 120, message = "name must be at most 120 characters")
        String name,

        @NotNull(message = "type is required")
        AccountType type,

        @NotNull(message = "initialBalance is required")
        @DecimalMin(value = "0.0", message = "initialBalance must be greater than or equal to 0")
        BigDecimal initialBalance,

        @Size(min = 3, max = 3, message = "currency must be a 3-letter ISO code (e.g. BRL)")
        String currency
) {
    public CreateAccountCommand toCreateCommand() {
        return new CreateAccountCommand(name, type, initialBalance, currency);
    }

    public UpdateAccountCommand toUpdateCommand() {
        return new UpdateAccountCommand(name, type, initialBalance, currency);
    }
}
