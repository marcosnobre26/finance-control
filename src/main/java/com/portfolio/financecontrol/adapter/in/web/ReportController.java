package com.portfolio.financecontrol.adapter.in.web;

import com.portfolio.financecontrol.adapter.in.web.dto.BalanceSummaryResponse;
import com.portfolio.financecontrol.application.port.in.ReportUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportUseCase reportUseCase;

    public ReportController(ReportUseCase reportUseCase) {
        this.reportUseCase = reportUseCase;
    }

    @GetMapping("/summary")
    public ResponseEntity<BalanceSummaryResponse> monthlySummary() {
        return ResponseEntity.ok(BalanceSummaryResponse.from(reportUseCase.getMonthlySummary()));
    }
}
