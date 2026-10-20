package com.portfolio.financecontrol.adapter.out.persistence.repository;

import com.portfolio.financecontrol.adapter.out.persistence.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

public interface AccountJpaRepository extends JpaRepository<AccountEntity, Long> {

    @Query("SELECT COALESCE(SUM(a.balance), 0) FROM AccountEntity a WHERE a.active = true")
    BigDecimal sumActiveBalances();
}
