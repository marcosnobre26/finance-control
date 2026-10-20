package com.portfolio.financecontrol.adapter.out.persistence.mapper;

import com.portfolio.financecontrol.adapter.out.persistence.entity.AccountEntity;
import com.portfolio.financecontrol.domain.model.Account;

public final class AccountMapper {

    private AccountMapper() {
    }

    public static AccountEntity toEntity(Account account) {
        AccountEntity entity = new AccountEntity();
        entity.setId(account.getId());
        entity.setName(account.getName());
        entity.setType(account.getType());
        entity.setBalance(account.getBalance());
        entity.setCurrency(account.getCurrency());
        entity.setActive(account.isActive());
        entity.setCreatedAt(account.getCreatedAt());
        entity.setUpdatedAt(account.getUpdatedAt());
        return entity;
    }

    public static Account toDomain(AccountEntity entity) {
        Account account = new Account();
        account.setId(entity.getId());
        account.setName(entity.getName());
        account.setType(entity.getType());
        account.setBalance(entity.getBalance());
        account.setCurrency(entity.getCurrency());
        account.setActive(entity.isActive());
        account.setCreatedAt(entity.getCreatedAt());
        account.setUpdatedAt(entity.getUpdatedAt());
        return account;
    }
}
