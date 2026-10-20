package com.portfolio.financecontrol.application.port.out;

import com.portfolio.financecontrol.domain.model.Account;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountPort {

    Account save(Account account);

    Optional<Account> findById(Long id);

    List<Account> findAll();

    void delete(Long id);

    BigDecimal sumActiveBalances();
}
