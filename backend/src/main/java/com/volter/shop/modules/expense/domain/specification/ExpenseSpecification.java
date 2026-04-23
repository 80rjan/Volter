package com.volter.shop.modules.expense.domain.specification;

import com.volter.shop.modules.expense.domain.model.Expense;
import com.volter.shop.modules.expense.application.dto.filter.ExpenseFilter;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ExpenseSpecification {

    public static Specification<Expense> withFilters(ExpenseFilter filters) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filters.getExpenseType() != null) {
                predicates.add(
                        cb.equal(cb.lower(root.get("expenseType")), filters.getExpenseType().toString().toLowerCase())
                );
            }

            if (filters.getFromDate() != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(root.get("date"), filters.getFromDate())
                );
            }

            if (filters.getToDate() != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(root.get("date"), filters.getToDate())
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Expense> withMonthAndYear(Integer month, Integer year) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (month != null) {
                predicates.add(
                        cb.equal(cb.function("MONTH", Integer.class, root.get("date")), month)
                );
            }

            if (year != null) {
                predicates.add(
                        cb.equal(cb.function("YEAR", Integer.class, root.get("date")), year)
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
