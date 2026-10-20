package com.portfolio.financecontrol.application.dto;

import java.math.BigDecimal;

public record BalanceSummaryResult(
        BigDecimal totalBalance,
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal netBalance
) {
}
