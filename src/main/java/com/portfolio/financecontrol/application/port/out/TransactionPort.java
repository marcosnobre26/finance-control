package com.portfolio.financecontrol.application.port.out;

import com.portfolio.financecontrol.domain.enums.TransactionType;
import com.portfolio.financecontrol.domain.model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionPort {

    Transaction save(Transaction transaction);

    Optional<Transaction> findById(Long id);

    List<Transaction> findAll();

    List<Transaction> findByAccountId(Long accountId);

    void delete(Long id);

    BigDecimal sumAmountByTypeAndDateBetween(TransactionType type, LocalDate startDate, LocalDate endDate);
}
