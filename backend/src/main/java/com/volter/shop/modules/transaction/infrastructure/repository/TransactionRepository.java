package com.volter.shop.modules.transaction.infrastructure.repository;

import com.volter.shop.modules.transaction.domain.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long>,
        JpaSpecificationExecutor<Transaction> {

    @Query("""
        SELECT t FROM Transaction t
        WHERE (TYPE(t) = PawnTransaction AND TREAT(t AS PawnTransaction).pawn.customer.id = :customerId)
           OR (TYPE(t) = SaleTransaction AND TREAT(t AS SaleTransaction).sale.customer.id = :customerId)
    """)
    List<Transaction> findByCustomerId(@Param("customerId") Long customerId);

    @Query(value = """
        SELECT
            t.transaction_category AS category,
            CASE
                WHEN t.transaction_category = 'PAWN'    THEN pi.item_type
                WHEN t.transaction_category = 'SALE'    THEN si.item_type
                ELSE NULL
            END                                                                                      AS type,
            COUNT(*)                                                                                 AS count,
            SUM(CASE WHEN t.direction = 'IN'  THEN t.amount ELSE 0 END)                             AS revenue,
            SUM(CASE WHEN t.direction = 'OUT' THEN t.amount ELSE 0 END)                             AS cash_out,
            SUM(CASE WHEN t.direction = 'IN'  THEN t.amount ELSE 0 END) +
                SUM(CASE WHEN t.direction = 'OUT' THEN t.amount ELSE 0 END)                         AS turnover,
            SUM(CASE WHEN t.margin_type = 'PROFIT' THEN t.margin_amount ELSE 0 END) -
                SUM(CASE WHEN t.margin_type = 'LOSS' THEN t.margin_amount ELSE 0 END)               AS gross_profit,
            SUM(CASE WHEN t.transaction_category = 'EXPENSE' THEN t.amount ELSE 0 END)              AS expenses,
            (SUM(CASE WHEN t.margin_type = 'PROFIT' THEN t.margin_amount ELSE 0 END) -
                SUM(CASE WHEN t.margin_type = 'LOSS' THEN t.margin_amount ELSE 0 END)) -
                SUM(CASE WHEN t.transaction_category = 'EXPENSE' THEN t.amount ELSE 0 END)          AS net_profit
        FROM transaction t
        LEFT JOIN pawn    p  ON t.pawn_id    = p.id
        LEFT JOIN item    pi ON p.item_id    = pi.id
        LEFT JOIN sale    s  ON t.sale_id    = s.id
        LEFT JOIN item    si ON s.item_id    = si.id
        LEFT JOIN expense e  ON t.expense_id = e.id
        WHERE EXTRACT(YEAR  FROM t.created_at) = :year
          AND EXTRACT(MONTH FROM t.created_at) = :month
        GROUP BY t.transaction_category, pi.item_type, si.item_type
        """, nativeQuery = true)
    List<TransactionAggregateRow> aggregateByMonthAndYear(@Param("year") Integer year, @Param("month") Integer month);

}
