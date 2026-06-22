package com.volter.shop.modules.expense.domain.repository;

import com.volter.shop.modules.expense.application.dto.ExpenseCategoryBucket;
import com.volter.shop.modules.expense.domain.model.Expense;
import com.volter.shop.modules.expense.domain.model.enums.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long>, JpaSpecificationExecutor<Expense> {

    /**
     * Per (year, month, category) totals for expenses recorded by the given staff,
     * narrowed by optional category and date range. Assembled into monthly
     * summaries (with per-category breakdown) by the service.
     */
    @Query("""
            select new com.volter.shop.modules.expense.application.dto.ExpenseCategoryBucket(
                extract(year from e.date),
                extract(month from e.date),
                e.category,
                sum(e.amount.amount),
                count(e))
            from Expense e
            where e.staffId in :staffIds
              and (:category is null or e.category = :category)
              and e.date >= :dateFrom
              and e.date <= :dateTo
            group by extract(year from e.date), extract(month from e.date), e.category
            """)
    List<ExpenseCategoryBucket> summarizeByMonthAndCategory(
            @Param("staffIds") Collection<Long> staffIds,
            @Param("category") ExpenseCategory category,
            @Param("dateFrom") LocalDate dateFrom,
            @Param("dateTo") LocalDate dateTo);
}
