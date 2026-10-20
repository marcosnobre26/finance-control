package com.portfolio.financecontrol.domain.model;

import com.portfolio.financecontrol.domain.enums.TransactionCategory;
import com.portfolio.financecontrol.domain.enums.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public class Transaction {

    private Long id;
    private Long accountId;
    private String accountName;
    private TransactionType type;
    private TransactionCategory category;
    private String description;
    private BigDecimal amount;
    private LocalDate date;
    private Instant createdAt;

    public static Transaction create(Long accountId, String accountName, TransactionType type,
                                     TransactionCategory category, String description,
                                     BigDecimal amount, LocalDate date) {
        Transaction transaction = new Transaction();
        transaction.accountId = accountId;
        transaction.accountName = accountName;
        transaction.type = type;
        transaction.category = category;
        transaction.description = description;
        transaction.amount = amount;
        transaction.date = date;
        transaction.createdAt = Instant.now();
        return transaction;
    }

    public BigDecimal signedAmount() {
        return type == TransactionType.INCOME ? amount : amount.negate();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public TransactionCategory getCategory() {
        return category;
    }

    public void setCategory(TransactionCategory category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
