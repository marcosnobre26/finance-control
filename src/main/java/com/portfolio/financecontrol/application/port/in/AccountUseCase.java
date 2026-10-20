package com.portfolio.financecontrol.application.port.in;

import com.portfolio.financecontrol.application.dto.AccountResult;
import com.portfolio.financecontrol.application.dto.CreateAccountCommand;
import com.portfolio.financecontrol.application.dto.UpdateAccountCommand;

import java.util.List;

public interface AccountUseCase {

    AccountResult create(CreateAccountCommand command);

    AccountResult findById(Long id);

    List<AccountResult> findAll();

    AccountResult update(Long id, UpdateAccountCommand command);

    void delete(Long id);
}
