package com.portfolio.financecontrol.adapter.out.persistence;

import com.portfolio.financecontrol.adapter.out.persistence.entity.AccountEntity;
import com.portfolio.financecontrol.adapter.out.persistence.entity.TransactionEntity;
import com.portfolio.financecontrol.adapter.out.persistence.mapper.TransactionMapper;
import com.portfolio.financecontrol.adapter.out.persistence.repository.AccountJpaRepository;
import com.portfolio.financecontrol.adapter.out.persistence.repository.TransactionJpaRepository;
import com.portfolio.financecontrol.application.port.out.TransactionPort;
import com.portfolio.financecontrol.domain.enums.TransactionType;
import com.portfolio.financecontrol.domain.model.Transaction;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class TransactionPersistenceAdapter implements TransactionPort {

    private final TransactionJpaRepository transactionRepository;
    private final AccountJpaRepository accountRepository;

    public TransactionPersistenceAdapter(TransactionJpaRepository transactionRepository,
                                         AccountJpaRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public Transaction save(Transaction transaction) {
        AccountEntity accountRef = accountRepository.getReferenceById(transaction.getAccountId());
        TransactionEntity saved = transactionRepository.save(TransactionMapper.toEntity(transaction, accountRef));
        return TransactionMapper.toDomain(saved);
    }

    @Override
    public Optional<Transaction> findById(Long id) {
        return transactionRepository.findById(id).map(TransactionMapper::toDomain);
    }

    @Override
    public List<Transaction> findAll() {
        return transactionRepository.findAll().stream()
                .map(TransactionMapper::toDomain)
                .toList();
    }

    @Override
    public List<Transaction> findByAccountId(Long accountId) {
        return transactionRepository.findByAccountId(accountId).stream()
                .map(TransactionMapper::toDomain)
                .toList();
    }

    @Override
    public void delete(Long id) {
        transactionRepository.deleteById(id);
    }

    @Override
    public BigDecimal sumAmountByTypeAndDateBetween(TransactionType type, LocalDate startDate, LocalDate endDate) {
        return transactionRepository.sumAmountByTypeAndDateBetween(type, startDate, endDate);
    }
}
