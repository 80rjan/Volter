package com.volter.shop.modules.expense.infrastructure;

import com.volter.shop.modules.expense.domain.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExpenseRepository
        extends JpaRepository<Expense, Long>,
        JpaSpecificationExecutor<Expense> {


    @Query("""
           SELECT e
           FROM Expense e
           WHERE (:month IS NULL OR MONTH(e.date) = :month)
             AND (:year IS NULL OR YEAR(e.date) = :year)
           """)
    List<Expense> findByMonthAndYear(
            @Param("month") Integer month,
            @Param("year") Integer year
    );
}
