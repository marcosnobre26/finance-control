package com.portfolio.financecontrol.domain.model;

import com.portfolio.financecontrol.domain.enums.AccountType;
import com.portfolio.financecontrol.domain.exception.BusinessException;

import java.math.BigDecimal;
import java.time.Instant;

public class Account {

    private Long id;
    private String name;
    private AccountType type;
    private BigDecimal balance = BigDecimal.ZERO;
    private String currency = "BRL";
    private boolean active = true;
    private Instant createdAt;
    private Instant updatedAt;

    public static Account create(String name, AccountType type, BigDecimal initialBalance, String currency) {
        Account account = new Account();
        account.setName(name);
        account.setType(type);
        account.setBalance(initialBalance == null ? BigDecimal.ZERO : initialBalance);
        account.setCurrency(currency);
        account.setActive(true);
        account.setCreatedAt(Instant.now());
        account.setUpdatedAt(Instant.now());
        return account;
    }

    public void credit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    public void debit(BigDecimal amount) {
        this.balance = this.balance.subtract(amount);
    }

    public void ensureActive() {
        if (!active) {
            throw new BusinessException("Conta inativa não pode receber transações: id=" + id);
        }
    }

    public void touch() {
        this.updatedAt = Instant.now();
    }

    private static String normalizeCurrency(String currency) {
        if (currency == null || currency.isBlank()) {
            return "BRL";
        }
        return currency.toUpperCase();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = normalizeCurrency(currency);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
