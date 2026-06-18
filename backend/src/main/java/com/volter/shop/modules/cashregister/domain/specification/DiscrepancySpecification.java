package com.volter.shop.modules.cashregister.domain.specification;

import com.volter.shop.modules.cashregister.application.dto.DiscrepancyFilterRequest;
import com.volter.shop.modules.cashregister.domain.model.CashRegisterSessionDiscrepancy;
import com.volter.shared.specification.PredicateBuilder;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;

public final class DiscrepancySpecification {

    private DiscrepancySpecification() {}

    /** Restricts to discrepancies whose session was operated by one of the given staff (empty set matches nothing). */
    public static Specification<CashRegisterSessionDiscrepancy> staffIdIn(Collection<Long> staffIds) {
        return (root, query, cb) -> staffIds.isEmpty() ? cb.disjunction() : root.get("session").get("staffId").in(staffIds);
    }

    /** Restricts to discrepancies whose session was operated by the given staff member. */
    public static Specification<CashRegisterSessionDiscrepancy> staffIdEquals(Long staffId) {
        return (root, query, cb) -> cb.equal(root.get("session").get("staffId"), staffId);
    }

    public static Specification<CashRegisterSessionDiscrepancy> matches(DiscrepancyFilterRequest filter) {
        return (root, query, cb) -> {
            var predicates = new PredicateBuilder<CashRegisterSessionDiscrepancy>(root, cb)
                    .withValue(root.get("session").get("id"), filter.sessionId())
                    .withValue(root.get("session").get("staffId"), filter.staffId())
                    .withEnum(root.get("type"), filter.type())
                    .withEnum(root.get("status"), filter.status())
                    .build();
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
