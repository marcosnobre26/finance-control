package com.portfolio.financecontrol.application.port.in;

import com.portfolio.financecontrol.application.dto.CreateTransactionCommand;
import com.portfolio.financecontrol.application.dto.TransactionResult;

import java.util.List;

public interface TransactionUseCase {

    TransactionResult create(CreateTransactionCommand command);

    TransactionResult findById(Long id);

    List<TransactionResult> findAll();

    List<TransactionResult> findByAccount(Long accountId);

    void delete(Long id);
}
