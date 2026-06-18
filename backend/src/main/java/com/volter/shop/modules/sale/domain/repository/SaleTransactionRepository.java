package com.volter.shop.modules.sale.domain.repository;

import com.volter.shop.modules.inventory.domain.model.enums.ItemType;
import com.volter.shop.modules.sale.domain.model.SaleTransaction;
import com.volter.shop.modules.transaction.application.dto.CashFlowSummary;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionDirection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface SaleTransactionRepository extends JpaRepository<SaleTransaction, Long> {

    default CashFlowSummary summarize(LocalDate from, LocalDate to, ItemType itemType) {
        return summarize(from, to, itemType,
                TransactionDirection.IN, TransactionDirection.OUT);
    }

    @Query("""
                    select
                        new com.volter.shop.modules.transaction.application.dto.CashFlowSummary(
                            count(st),
                            coalesce(sum(case when st.transaction.direction = :inDirection then st.transaction.amount.amount else 0 end), 0),
                            coalesce(sum(case when st.transaction.direction = :outDirection then st.transaction.amount.amount else 0 end), 0),
                            coalesce(sum(case when st.transaction.direction = :inDirection then st.transaction.amount.amount else 0 end), 0) -
                            coalesce(sum(case when st.transaction.direction = :outDirection then st.transaction.amount.amount else 0 end), 0)
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
