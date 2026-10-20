package com.portfolio.financecontrol.adapter.out.persistence.repository;

import com.portfolio.financecontrol.adapter.out.persistence.entity.TransactionEntity;
import com.portfolio.financecontrol.domain.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TransactionJpaRepository extends JpaRepository<TransactionEntity, Long> {

    List<TransactionEntity> findByAccountId(Long accountId);

    @Query("""
            SELECT COALESCE(SUM(t.amount), 0)
            FROM TransactionEntity t
            WHERE t.type = :type
              AND t.date BETWEEN :startDate AND :endDate
            """)
    BigDecimal sumAmountByTypeAndDateBetween(
            @Param("type") TransactionType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
