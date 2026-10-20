package com.portfolio.financecontrol.application.service;

import com.portfolio.financecontrol.application.dto.CreateTransactionCommand;
import com.portfolio.financecontrol.application.dto.TransactionResult;
import com.portfolio.financecontrol.application.port.in.TransactionUseCase;
import com.portfolio.financecontrol.application.port.out.AccountPort;
import com.portfolio.financecontrol.application.port.out.EventPublisherPort;
import com.portfolio.financecontrol.application.port.out.TransactionPort;
import com.portfolio.financecontrol.domain.enums.TransactionType;
import com.portfolio.financecontrol.domain.event.LowBalanceEvent;
import com.portfolio.financecontrol.domain.event.TransactionCreatedEvent;
import com.portfolio.financecontrol.domain.exception.ResourceNotFoundException;
import com.portfolio.financecontrol.domain.model.Account;
import com.portfolio.financecontrol.domain.model.Transaction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionService implements TransactionUseCase {

    private final TransactionPort transactionPort;
    private final AccountPort accountPort;
    private final EventPublisherPort eventPublisher;

    public TransactionService(TransactionPort transactionPort, AccountPort accountPort, EventPublisherPort eventPublisher) {
        this.transactionPort = transactionPort;
        this.accountPort = accountPort;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public TransactionResult create(CreateTransactionCommand command) {
        Account account = accountPort.findById(command.accountId())
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada: id=" + command.accountId()));
        account.ensureActive();

        Transaction transaction = Transaction.create(
                account.getId(),
                account.getName(),
                command.type(),
                command.category(),
                command.description(),
                command.amount(),
                command.date()
        );

        Transaction saved = transactionPort.save(transaction);

        applyBalanceChange(account, command.type(), command.amount());
        account.touch();
        accountPort.save(account);

        eventPublisher.publish(TransactionCreatedEvent.from(saved));

        if (account.getBalance().signum() < 0) {
            eventPublisher.publish(LowBalanceEvent.from(account));
        }

        return TransactionResult.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionResult findById(Long id) {
        return TransactionResult.from(findTransaction(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResult> findAll() {
        return transactionPort.findAll().stream()
                .map(TransactionResult::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResult> findByAccount(Long accountId) {
        return transactionPort.findByAccountId(accountId).stream()
                .map(TransactionResult::from)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Transaction transaction = findTransaction(id);
        Account account = accountPort.findById(transaction.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada: id=" + transaction.getAccountId()));

        // Reverte o impacto no saldo da conta antes de remover a transação.
        if (transaction.getType() == TransactionType.INCOME) {
            account.debit(transaction.getAmount());
        } else {
            account.credit(transaction.getAmount());
        }
        account.touch();
        accountPort.save(account);

        transactionPort.delete(id);
    }

    private void applyBalanceChange(Account account, TransactionType type, BigDecimal amount) {
        if (type == TransactionType.INCOME) {
            account.credit(amount);
        } else {
            account.debit(amount);
        }
    }

    private Transaction findTransaction(Long id) {
        return transactionPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada: id=" + id));
    }
}
