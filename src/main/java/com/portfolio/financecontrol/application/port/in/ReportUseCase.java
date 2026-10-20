package com.portfolio.financecontrol.application.port.in;

import com.portfolio.financecontrol.application.dto.BalanceSummaryResult;

public interface ReportUseCase {

    BalanceSummaryResult getMonthlySummary();
}
