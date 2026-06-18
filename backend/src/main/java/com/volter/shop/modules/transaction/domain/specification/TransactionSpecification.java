package com.volter.shop.modules.transaction.domain.specification;

import com.volter.shop.modules.transaction.application.dto.TransactionFilterRequest;
import com.volter.shop.modules.transaction.domain.model.Transaction;
import com.volter.shared.specification.PredicateBuilder;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;

public final class TransactionSpecification {

    private TransactionSpecification() {}

    /** Restricts to transactions made by one of the given staff members (empty set matches nothing). */
    public static Specification<Transaction> staffIdIn(Collection<Long> staffIds) {
        return (root, query, cb) -> staffIds.isEmpty() ? cb.disjunction() : root.get("staffId").in(staffIds);
    }

    public static Specification<Transaction> matches(TransactionFilterRequest filter) {
        return (root, query, cb) -> {
            var predicates = new PredicateBuilder<Transaction>(root, cb)
                    .withEnum(root.get("type"), filter.type())
                    .withEnum(root.get("direction"), filter.direction())
                    .withRange(root.get("createdAt"), filter.createdFrom(), filter.createdTo())
                    .build();
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
