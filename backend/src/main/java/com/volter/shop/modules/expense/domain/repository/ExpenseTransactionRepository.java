package com.volter.shop.modules.expense.domain.repository;

import com.volter.shop.modules.expense.application.dto.ExpenseSummary;
import com.volter.shop.modules.expense.domain.model.ExpenseTransaction;
import com.volter.shop.modules.expense.domain.model.enums.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface ExpenseTransactionRepository extends JpaRepository<ExpenseTransaction, Long> {

    @Query("""
                    select
                        new com.volter.shop.modules.expense.application.dto.ExpenseSummary(
                            count(*),
                            coalesce(sum(et.transaction.amount.amount), 0)
                        )
                    from ExpenseTransaction et
                    where cast(et.transaction.createdAt as date) >= :from
                      and cast(et.transaction.createdAt as date) <= :to
                      and (et.expense.category = :expenseCategory)
            """)
    ExpenseSummary summarize(@Param("from") LocalDate from,
                             @Param("to") LocalDate to,
                             @Param("expenseCategory") ExpenseCategory expenseCategory
                             );
}
