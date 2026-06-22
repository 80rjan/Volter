package com.volter.shop.modules.expense.domain.repository;

import com.volter.shop.modules.expense.application.dto.ExpenseSummary;
import com.volter.shop.modules.expense.domain.model.ExpenseTransaction;
import com.volter.shop.modules.expense.domain.model.enums.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface ExpenseTransactionRepository extends JpaRepository<ExpenseTransaction, Long> {

    /** Expense id behind a given transaction, for assembling its detailed view. */
    @Query("select et.expense.id from ExpenseTransaction et where et.transaction.id = :transactionId")
    Optional<Long> findExpenseIdByTransactionId(@Param("transactionId") Long transactionId);

    /** Count of expenses a staff member recorded in a date range, for the staff performance report. */
    @Query("""
            select count(et) from ExpenseTransaction et
            where et.transaction.staffId = :staffId
              and cast(et.transaction.createdAt as date) between :from and :to
            """)
    long countForStaffInRange(@Param("staffId") Long staffId,
                              @Param("from") LocalDate from,
                              @Param("to") LocalDate to);

    @Query("""
                    select
                        new com.volter.shop.modules.expense.application.dto.ExpenseSummary(
                            count(*),
                            coalesce(sum(et.transaction.amount.amount), 0L)
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
