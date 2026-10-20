package com.portfolio.financecontrol.adapter.in.web;

import com.portfolio.financecontrol.adapter.in.web.dto.AccountRequest;
import com.portfolio.financecontrol.adapter.in.web.dto.AccountResponse;
import com.portfolio.financecontrol.application.port.in.AccountUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountUseCase accountUseCase;

    public AccountController(AccountUseCase accountUseCase) {
        this.accountUseCase = accountUseCase;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> create(@Valid @RequestBody AccountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AccountResponse.from(accountUseCase.create(request.toCreateCommand())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(AccountResponse.from(accountUseCase.findById(id)));
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> findAll() {
        List<AccountResponse> response = accountUseCase.findAll().stream()
                .map(AccountResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountResponse> update(@PathVariable Long id, @Valid @RequestBody AccountRequest request) {
        return ResponseEntity.ok(AccountResponse.from(accountUseCase.update(id, request.toUpdateCommand())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        accountUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}
