package com.volter.shop.modules.pawn.domain.repository;

import com.volter.shop.modules.inventory.domain.model.enums.ItemType;
import com.volter.shop.modules.pawn.domain.model.PawnTransaction;
import com.volter.shop.modules.transaction.application.dto.CashFlowSummary;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionDirection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface PawnTransactionRepository extends JpaRepository<PawnTransaction, Long> {

    default CashFlowSummary summarize(LocalDate from, LocalDate to, ItemType itemType) {
        return summarize(from, to, itemType,
                TransactionDirection.IN, TransactionDirection.OUT);
    }

    @Query("""
                    select
                        new com.volter.shop.modules.transaction.application.dto.CashFlowSummary(
                            count(pt),
                            coalesce(sum(case when pt.transaction.direction = :inDirection then pt.transaction.amount.amount else 0 end), 0),
                            coalesce(sum(case when pt.transaction.direction = :outDirection then pt.transaction.amount.amount else 0 end), 0),
                            coalesce(sum(case when pt.transaction.direction = :inDirection then pt.transaction.amount.amount else 0 end), 0) -
                            coalesce(sum(case when pt.transaction.direction = :outDirection then pt.transaction.amount.amount else 0 end), 0)
                        )
                    from PawnTransaction pt
                    where cast(pt.transaction.createdAt as date) >= :from
                      and cast(pt.transaction.createdAt as date) <= :to
                      and pt.pawnContract.item.type = :itemType
            """)
    CashFlowSummary summarize(@Param("from") LocalDate from,
                              @Param("to") LocalDate to,
                              @Param("itemType") ItemType itemType,
                              @Param("inDirection") TransactionDirection inDirection,
                              @Param("outDirection") TransactionDirection outDirection
    );
}
