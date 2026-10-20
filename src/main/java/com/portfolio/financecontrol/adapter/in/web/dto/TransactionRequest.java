package com.portfolio.financecontrol.adapter.in.web.dto;

import com.portfolio.financecontrol.application.dto.CreateTransactionCommand;
import com.portfolio.financecontrol.domain.enums.TransactionCategory;
import com.portfolio.financecontrol.domain.enums.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionRequest(
        @NotNull(message = "accountId is required")
        Long accountId,

        @NotNull(message = "type is required")
        TransactionType type,

        @NotNull(message = "category is required")
        TransactionCategory category,

        @NotBlank(message = "description is required")
        @Size(max = 255, message = "description must be at most 255 characters")
        String description,

        @NotNull(message = "amount is required")
        @Positive(message = "amount must be greater than 0")
        BigDecimal amount,

        @NotNull(message = "date is required")
        LocalDate date
) {
    public CreateTransactionCommand toCommand() {
        return new CreateTransactionCommand(accountId, type, category, description, amount, date);
    }
}
