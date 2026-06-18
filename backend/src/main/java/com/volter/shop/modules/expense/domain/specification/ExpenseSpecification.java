package com.volter.shop.modules.expense.domain.specification;

import com.volter.shop.modules.expense.application.dto.ExpenseFilterRequest;
import com.volter.shop.modules.expense.domain.model.Expense;
import com.volter.shared.specification.PredicateBuilder;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;

public final class ExpenseSpecification {

    private ExpenseSpecification() {}

    /** Restricts to expenses recorded by one of the given staff members (empty set matches nothing). */
    public static Specification<Expense> staffIdIn(Collection<Long> staffIds) {
        return (root, query, cb) -> staffIds.isEmpty() ? cb.disjunction() : root.get("staffId").in(staffIds);
    }

    public static Specification<Expense> matches(ExpenseFilterRequest filter) {
        return (root, query, cb) -> {
            var predicates = new PredicateBuilder<Expense>(root, cb)
                    .withEnum(root.get("category"), filter.category())
                    .withValue(root.get("staffId"), filter.staffId())
                    .withDateRange(root.get("date"), filter.dateFrom(), filter.dateTo())
                    .build();
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
