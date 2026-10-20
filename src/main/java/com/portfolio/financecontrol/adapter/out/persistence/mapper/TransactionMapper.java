package com.portfolio.financecontrol.adapter.out.persistence.mapper;

import com.portfolio.financecontrol.adapter.out.persistence.entity.AccountEntity;
import com.portfolio.financecontrol.adapter.out.persistence.entity.TransactionEntity;
import com.portfolio.financecontrol.domain.model.Transaction;

public final class TransactionMapper {

    private TransactionMapper() {
    }

    public static TransactionEntity toEntity(Transaction transaction, AccountEntity accountRef) {
        TransactionEntity entity = new TransactionEntity();
        entity.setId(transaction.getId());
        entity.setAccount(accountRef);
        entity.setAccountName(transaction.getAccountName());
        entity.setType(transaction.getType());
        entity.setCategory(transaction.getCategory());
        entity.setDescription(transaction.getDescription());
        entity.setAmount(transaction.getAmount());
        entity.setDate(transaction.getDate());
        entity.setCreatedAt(transaction.getCreatedAt());
        return entity;
    }

    public static Transaction toDomain(TransactionEntity entity) {
        Transaction transaction = new Transaction();
        transaction.setId(entity.getId());
        transaction.setAccountId(entity.getAccount().getId());
        transaction.setAccountName(entity.getAccountName());
        transaction.setType(entity.getType());
        transaction.setCategory(entity.getCategory());
        transaction.setDescription(entity.getDescription());
        transaction.setAmount(entity.getAmount());
        transaction.setDate(entity.getDate());
        transaction.setCreatedAt(entity.getCreatedAt());
        return transaction;
    }
}
