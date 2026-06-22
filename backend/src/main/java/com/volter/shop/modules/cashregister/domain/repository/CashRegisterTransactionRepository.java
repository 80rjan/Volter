package com.volter.shop.modules.cashregister.domain.repository;

import com.volter.shop.modules.cashregister.application.dto.CashRegisterSessionSummary;
import com.volter.shop.modules.cashregister.application.dto.CashRegisterSummary;
import com.volter.shop.modules.cashregister.domain.model.CashRegisterTransaction;
import com.volter.shop.modules.cashregister.domain.model.enums.CashRegisterTransactionAction;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionDirection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface CashRegisterTransactionRepository extends JpaRepository<CashRegisterTransaction, Long> {

    default CashRegisterSummary summarize(LocalDate from, LocalDate to) {
        return summarize(from, to,
                TransactionDirection.IN, TransactionDirection.OUT, CashRegisterTransactionAction.WITHDRAWAL, CashRegisterTransactionAction.DEPOSIT, CashRegisterTransactionAction.ADJUSTMENT);
    }

    @Query("""
                    select
                        new com.volter.shop.modules.cashregister.application.dto.CashRegisterSummary(
                            count(*),
                            coalesce(sum(case when crt.transaction.direction = :inDirection then crt.transaction.amount.amount else 0 end), 0L),
                            coalesce(sum(case when crt.transaction.direction = :outDirection then crt.transaction.amount.amount else 0 end), 0L),
                            coalesce(sum(case when crt.transaction.direction = :inDirection then crt.transaction.amount.amount else 0 end), 0L) -
                            coalesce(sum(case when crt.transaction.direction = :outDirection then crt.transaction.amount.amount else 0 end), 0L),
                            coalesce(sum(case when crt.action = :withdrawalAction then crt.transaction.amount.amount else 0 end), 0L),
                            coalesce(sum(case when crt.action = :depositAction then crt.transaction.amount.amount else 0 end), 0L),
                            coalesce(sum(case when crt.action = :adjustmentAction then crt.transaction.amount.amount else 0 end), 0L)
                        )
                    from CashRegisterTransaction crt
                    where cast(crt.transaction.createdAt as date) >= :from
                      and cast(crt.transaction.createdAt as date) <= :to
            """)
    CashRegisterSummary summarize(@Param("from") LocalDate from,
                                  @Param("to") LocalDate to,
                                  @Param("inDirection") TransactionDirection inDirection,
                                  @Param("outDirection") TransactionDirection outDirection,
                                  @Param("withdrawalAction") CashRegisterTransactionAction withdrawalAction,
                                  @Param("depositAction") CashRegisterTransactionAction depositAction,
                                  @Param("adjustmentAction") CashRegisterTransactionAction adjustmentAction
    );

    default List<CashRegisterSessionSummary> summarizeSessions(LocalDate from, LocalDate to) {
        return summarizeSessions(from, to,
                TransactionDirection.IN, TransactionDirection.OUT);
    }

    @Query("""
                    select
                        new com.volter.shop.modules.cashregister.application.dto.CashRegisterSessionSummary(
                            crt.transaction.cashRegisterSession.id,
                            cast(crt.transaction.cashRegisterSession.openedAt as LocalDate),
                            count(*),
                            coalesce(sum(case when crt.transaction.direction = :inDirection then crt.transaction.amount.amount else 0 end), 0L),
                            coalesce(sum(case when crt.transaction.direction = :outDirection then crt.transaction.amount.amount else 0 end), 0L),
                            coalesce(sum(case when crt.transaction.direction = :inDirection then crt.transaction.amount.amount else 0 end), 0L) -
                            coalesce(sum(case when crt.transaction.direction = :outDirection then crt.transaction.amount.amount else 0 end), 0L)
                        )
                    from CashRegisterTransaction crt
                    where cast(crt.transaction.createdAt as date) >= :from
                      and cast(crt.transaction.createdAt as date) <= :to
                    group by crt.transaction.cashRegisterSession.id, crt.transaction.cashRegisterSession.openedAt
            """)
    List<CashRegisterSessionSummary> summarizeSessions(@Param("from") LocalDate from,
                                                      @Param("to") LocalDate to,
                                                      @Param("inDirection") TransactionDirection inDirection,
                                                      @Param("outDirection") TransactionDirection outDirection
    );
}
