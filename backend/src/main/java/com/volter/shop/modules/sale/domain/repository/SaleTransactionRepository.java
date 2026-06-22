package com.volter.shop.modules.sale.domain.repository;

import com.volter.shop.modules.inventory.domain.model.enums.ItemType;
import com.volter.shop.modules.sale.domain.model.SaleTransaction;
import com.volter.shop.modules.transaction.application.dto.CashFlowSummary;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionDirection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SaleTransactionRepository extends JpaRepository<SaleTransaction, Long> {

    /** Sale id behind a given transaction, for assembling its detailed view. */
    @Query("select st.sale.id from SaleTransaction st where st.transaction.id = :transactionId")
    Optional<Long> findSaleIdByTransactionId(@Param("transactionId") Long transactionId);

    /** A staff member's sale transactions in a date range (with transaction + sale), for the staff performance report. */
    @Query("""
            select st from SaleTransaction st
            join fetch st.transaction t
            join fetch st.sale s
            where t.staffId = :staffId
              and cast(t.createdAt as date) between :from and :to
            """)
    List<SaleTransaction> findForStaffInRange(@Param("staffId") Long staffId,
                                              @Param("from") LocalDate from,
                                              @Param("to") LocalDate to);

    default CashFlowSummary summarize(LocalDate from, LocalDate to, ItemType itemType) {
        return summarize(from, to, itemType,
                TransactionDirection.IN, TransactionDirection.OUT);
    }

    @Query("""
                    select
                        new com.volter.shop.modules.transaction.application.dto.CashFlowSummary(
                            count(st),
                            coalesce(sum(case when st.transaction.direction = :inDirection then st.transaction.amount.amount else 0 end), 0L),
                            coalesce(sum(case when st.transaction.direction = :outDirection then st.transaction.amount.amount else 0 end), 0L),
                            coalesce(sum(case when st.transaction.direction = :inDirection then st.transaction.amount.amount else 0 end), 0L) -
                            coalesce(sum(case when st.transaction.direction = :outDirection then st.transaction.amount.amount else 0 end), 0L)
                        )
                    from SaleTransaction st
                    where cast(st.transaction.createdAt as date) >= :from
                      and cast(st.transaction.createdAt as date) <= :to
                      and st.sale.item.type = :itemType
            """)
    CashFlowSummary summarize(@Param("from") LocalDate from,
                              @Param("to") LocalDate to,
                              @Param("itemType") ItemType itemType,
                              @Param("inDirection") TransactionDirection inDirection,
                              @Param("outDirection") TransactionDirection outDirection
    );
}
