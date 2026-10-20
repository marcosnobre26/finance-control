package com.portfolio.financecontrol.adapter.in.web.dto;

import com.portfolio.financecontrol.application.dto.BalanceSummaryResult;

import java.math.BigDecimal;

public record BalanceSummaryResponse(
        BigDecimal totalBalance,
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal netBalance
) {
    public static BalanceSummaryResponse from(BalanceSummaryResult result) {
        return new BalanceSummaryResponse(
                result.totalBalance(),
                result.totalIncome(),
                result.totalExpense(),
                result.netBalance()
        );
    }
}
