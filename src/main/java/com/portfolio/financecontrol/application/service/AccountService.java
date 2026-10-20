package com.portfolio.financecontrol.application.service;

import com.portfolio.financecontrol.application.dto.AccountResult;
import com.portfolio.financecontrol.application.dto.CreateAccountCommand;
import com.portfolio.financecontrol.application.dto.UpdateAccountCommand;
import com.portfolio.financecontrol.application.port.in.AccountUseCase;
import com.portfolio.financecontrol.application.port.out.AccountPort;
import com.portfolio.financecontrol.domain.exception.ResourceNotFoundException;
import com.portfolio.financecontrol.domain.model.Account;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AccountService implements AccountUseCase {

    private final AccountPort accountPort;

    public AccountService(AccountPort accountPort) {
        this.accountPort = accountPort;
    }

    @Override
    @Transactional
    public AccountResult create(CreateAccountCommand command) {
        Account account = Account.create(command.name(), command.type(), command.initialBalance(), command.currency());
        return AccountResult.from(accountPort.save(account));
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResult findById(Long id) {
        return AccountResult.from(findAccount(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountResult> findAll() {
        return accountPort.findAll().stream()
                .map(AccountResult::from)
                .toList();
    }

    @Override
    @Transactional
    public AccountResult update(Long id, UpdateAccountCommand command) {
        Account account = findAccount(id);
        account.setName(command.name());
        account.setType(command.type());
        if (command.balance() != null) {
            account.setBalance(command.balance());
        }
        if (command.currency() != null && !command.currency().isBlank()) {
            account.setCurrency(command.currency());
        }
        account.touch();
        return AccountResult.from(accountPort.save(account));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        findAccount(id);
        accountPort.delete(id);
    }

    private Account findAccount(Long id) {
        return accountPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada: id=" + id));
    }
}
