package com.volter.shop.modules.pawn.domain.repository;

import com.volter.shop.modules.inventory.domain.model.enums.ItemType;
import com.volter.shop.modules.pawn.domain.model.PawnTransaction;
import com.volter.shop.modules.transaction.application.dto.CashFlowSummary;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionDirection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PawnTransactionRepository extends JpaRepository<PawnTransaction, Long> {

    /** Pawn contract id behind a given transaction, for assembling its detailed view. */
    @Query("select pt.pawnContract.id from PawnTransaction pt where pt.transaction.id = :transactionId")
    Optional<Long> findPawnContractIdByTransactionId(@Param("transactionId") Long transactionId);

    /** A staff member's pawn transactions in a date range (with transaction + contract), for the staff performance report. */
    @Query("""
            select pt from PawnTransaction pt
            join fetch pt.transaction t
            join fetch pt.pawnContract c
            where t.staffId = :staffId
              and cast(t.createdAt as date) between :from and :to
            """)
    List<PawnTransaction> findForStaffInRange(@Param("staffId") Long staffId,
                                              @Param("from") LocalDate from,
                                              @Param("to") LocalDate to);

    default CashFlowSummary summarize(LocalDate from, LocalDate to, ItemType itemType) {
        return summarize(from, to, itemType,
                TransactionDirection.IN, TransactionDirection.OUT);
    }

    @Query("""
                    select
                        new com.volter.shop.modules.transaction.application.dto.CashFlowSummary(
                            count(pt),
                            coalesce(sum(case when pt.transaction.direction = :inDirection then pt.transaction.amount.amount else 0 end), 0L),
                            coalesce(sum(case when pt.transaction.direction = :outDirection then pt.transaction.amount.amount else 0 end), 0L),
                            coalesce(sum(case when pt.transaction.direction = :inDirection then pt.transaction.amount.amount else 0 end), 0L) -
                            coalesce(sum(case when pt.transaction.direction = :outDirection then pt.transaction.amount.amount else 0 end), 0L)
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
