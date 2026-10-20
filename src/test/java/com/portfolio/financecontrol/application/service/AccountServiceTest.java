package com.portfolio.financecontrol.application.service;

import com.portfolio.financecontrol.application.dto.AccountResult;
import com.portfolio.financecontrol.application.dto.CreateAccountCommand;
import com.portfolio.financecontrol.application.port.out.AccountPort;
import com.portfolio.financecontrol.domain.enums.AccountType;
import com.portfolio.financecontrol.domain.exception.ResourceNotFoundException;
import com.portfolio.financecontrol.domain.model.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountPort accountPort;

    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountService = new AccountService(accountPort);
    }

    @Test
    void shouldCreateAccountWithDefaultCurrency() {
        CreateAccountCommand command = new CreateAccountCommand(
                "Conta Corrente",
                AccountType.CHECKING,
                new BigDecimal("1000.00"),
                null
        );

        Account saved = Account.create(command.name(), command.type(), command.initialBalance(), command.currency());
        saved.setId(1L);

        when(accountPort.save(any(Account.class))).thenReturn(saved);

        AccountResult result = accountService.create(command);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Conta Corrente");
        assertThat(result.currency()).isEqualTo("BRL");
        assertThat(result.balance()).isEqualByComparingTo("1000.00");
    }

    @Test
    void shouldThrowWhenAccountNotFound() {
        when(accountPort.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }
}
