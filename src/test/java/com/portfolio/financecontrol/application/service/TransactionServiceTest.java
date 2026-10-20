package com.portfolio.financecontrol.application.service;

import com.portfolio.financecontrol.application.dto.CreateTransactionCommand;
import com.portfolio.financecontrol.application.dto.TransactionResult;
import com.portfolio.financecontrol.application.port.out.AccountPort;
import com.portfolio.financecontrol.application.port.out.EventPublisherPort;
import com.portfolio.financecontrol.application.port.out.TransactionPort;
import com.portfolio.financecontrol.domain.enums.AccountType;
import com.portfolio.financecontrol.domain.enums.TransactionCategory;
import com.portfolio.financecontrol.domain.enums.TransactionType;
import com.portfolio.financecontrol.domain.event.LowBalanceEvent;
import com.portfolio.financecontrol.domain.event.TransactionCreatedEvent;
import com.portfolio.financecontrol.domain.exception.BusinessException;
import com.portfolio.financecontrol.domain.model.Account;
import com.portfolio.financecontrol.domain.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionPort transactionPort;

    @Mock
    private AccountPort accountPort;

    @Mock
    private EventPublisherPort eventPublisher;

    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        transactionService = new TransactionService(transactionPort, accountPort, eventPublisher);
    }

    @Test
    void shouldIncreaseBalanceAndPublishEventOnIncome() {
        Account account = activeAccount(new BigDecimal("500.00"));

        CreateTransactionCommand command = new CreateTransactionCommand(
                1L,
                TransactionType.INCOME,
                TransactionCategory.SALARY,
                "Salário mensal",
                new BigDecimal("2000.00"),
                LocalDate.now()
        );

        Transaction saved = Transaction.create(account.getId(), account.getName(), command.type(), command.category(),
                command.description(), command.amount(), command.date());
        saved.setId(10L);

        when(accountPort.findById(1L)).thenReturn(Optional.of(account));
        when(transactionPort.save(any(Transaction.class))).thenReturn(saved);

        TransactionResult result = transactionService.create(command);

        assertThat(account.getBalance()).isEqualByComparingTo("2500.00");
        assertThat(result.amount()).isEqualByComparingTo("2000.00");
        verify(eventPublisher).publish(any(TransactionCreatedEvent.class));
        verify(eventPublisher, never()).publish(any(LowBalanceEvent.class));
    }

    @Test
    void shouldRejectTransactionOnInactiveAccount() {
        Account account = activeAccount(BigDecimal.ZERO);
        account.setActive(false);

        CreateTransactionCommand command = new CreateTransactionCommand(
                1L,
                TransactionType.INCOME,
                TransactionCategory.OTHER,
                "Depósito",
                BigDecimal.TEN,
                LocalDate.now()
        );

        when(accountPort.findById(1L)).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> transactionService.create(command))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("inativa");
    }

    @Test
    void shouldPublishLowBalanceEventWhenBalanceGoesNegative() {
        Account account = activeAccount(new BigDecimal("100.00"));

        CreateTransactionCommand command = new CreateTransactionCommand(
                1L,
                TransactionType.EXPENSE,
                TransactionCategory.FOOD,
                "Mercado",
                new BigDecimal("250.00"),
                LocalDate.now()
        );

        Transaction saved = Transaction.create(account.getId(), account.getName(), command.type(), command.category(),
                command.description(), command.amount(), command.date());
        saved.setId(11L);

        when(accountPort.findById(1L)).thenReturn(Optional.of(account));
        when(transactionPort.save(any(Transaction.class))).thenReturn(saved);

        transactionService.create(command);

        assertThat(account.getBalance()).isEqualByComparingTo("-150.00");
        verify(eventPublisher).publish(any(TransactionCreatedEvent.class));
        verify(eventPublisher).publish(any(LowBalanceEvent.class));
    }

    private Account activeAccount(BigDecimal balance) {
        Account account = Account.create("Conta Corrente", AccountType.CHECKING, balance, "BRL");
        account.setId(1L);
        return account;
    }
}
