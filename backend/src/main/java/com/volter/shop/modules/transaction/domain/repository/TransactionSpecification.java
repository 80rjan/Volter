package com.volter.shop.modules.transaction.domain.repository;

import com.volter.shop.modules.transaction.domain.model.Transaction;
import com.volter.shop.modules.transaction.application.dto.filter.TransactionFilter;
import com.volter.shop.modules.transaction.domain.model.Transaction_;
import com.volter.shop.shared.specification.PredicateBuilder;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class TransactionSpecification {

    // todo: implement customer filters
    public static Specification<Transaction> withFilters(TransactionFilter filters) {
        return (root, query, cb) -> {

            var predicates = new PredicateBuilder<Transaction>(root, cb)
                    .withEnum(root.get(Transaction_.transactionCategory), filters.getTransactionCategory())
                    .withEnum(root.get(Transaction_.direction), filters.getDirection())
                    .withEnum(root.get(Transaction_.marginType), filters.getMarginType())
                    .build();

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
