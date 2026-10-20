package com.portfolio.financecontrol.adapter.in.web;

import com.portfolio.financecontrol.adapter.in.web.dto.TransactionRequest;
import com.portfolio.financecontrol.adapter.in.web.dto.TransactionResponse;
import com.portfolio.financecontrol.application.port.in.TransactionUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionUseCase transactionUseCase;

    public TransactionController(TransactionUseCase transactionUseCase) {
        this.transactionUseCase = transactionUseCase;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> create(@Valid @RequestBody TransactionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TransactionResponse.from(transactionUseCase.create(request.toCommand())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(TransactionResponse.from(transactionUseCase.findById(id)));
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> findAll(@RequestParam(required = false) Long accountId) {
        List<TransactionResponse> response = (accountId != null
                ? transactionUseCase.findByAccount(accountId)
                : transactionUseCase.findAll())
                .stream()
                .map(TransactionResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        transactionUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}
