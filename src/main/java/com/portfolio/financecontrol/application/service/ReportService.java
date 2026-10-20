package com.portfolio.financecontrol.application.service;

import com.portfolio.financecontrol.application.dto.BalanceSummaryResult;
import com.portfolio.financecontrol.application.port.in.ReportUseCase;
import com.portfolio.financecontrol.application.port.out.AccountPort;
import com.portfolio.financecontrol.application.port.out.TransactionPort;
import com.portfolio.financecontrol.domain.enums.TransactionType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@Service
public class ReportService implements ReportUseCase {

    private final AccountPort accountPort;
    private final TransactionPort transactionPort;

    public ReportService(AccountPort accountPort, TransactionPort transactionPort) {
        this.accountPort = accountPort;
        this.transactionPort = transactionPort;
    }

    @Override
    @Transactional(readOnly = true)
    public BalanceSummaryResult getMonthlySummary() {
        YearMonth currentMonth = YearMonth.now();
        LocalDate start = currentMonth.atDay(1);
        LocalDate end = currentMonth.atEndOfMonth();

        BigDecimal totalBalance = accountPort.sumActiveBalances();
        BigDecimal totalIncome = transactionPort.sumAmountByTypeAndDateBetween(TransactionType.INCOME, start, end);
        BigDecimal totalExpense = transactionPort.sumAmountByTypeAndDateBetween(TransactionType.EXPENSE, start, end);

        return new BalanceSummaryResult(
                totalBalance,
                totalIncome,
                totalExpense,
                totalIncome.subtract(totalExpense)
        );
    }
}
