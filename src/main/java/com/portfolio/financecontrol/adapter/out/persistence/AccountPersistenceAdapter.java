package com.portfolio.financecontrol.adapter.out.persistence;

import com.portfolio.financecontrol.adapter.out.persistence.entity.AccountEntity;
import com.portfolio.financecontrol.adapter.out.persistence.mapper.AccountMapper;
import com.portfolio.financecontrol.adapter.out.persistence.repository.AccountJpaRepository;
import com.portfolio.financecontrol.application.port.out.AccountPort;
import com.portfolio.financecontrol.domain.model.Account;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public class AccountPersistenceAdapter implements AccountPort {

    private final AccountJpaRepository repository;

    public AccountPersistenceAdapter(AccountJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Account save(Account account) {
        AccountEntity saved = repository.save(AccountMapper.toEntity(account));
        return AccountMapper.toDomain(saved);
    }

    @Override
    public Optional<Account> findById(Long id) {
        return repository.findById(id).map(AccountMapper::toDomain);
    }

    @Override
    public List<Account> findAll() {
        return repository.findAll().stream()
                .map(AccountMapper::toDomain)
                .toList();
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public BigDecimal sumActiveBalances() {
        return repository.sumActiveBalances();
    }
}
