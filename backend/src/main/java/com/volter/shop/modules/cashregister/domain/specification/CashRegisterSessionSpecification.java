package com.volter.shop.modules.cashregister.domain.specification;

import com.volter.shop.modules.cashregister.application.dto.SessionFilterRequest;
import com.volter.shop.modules.cashregister.domain.model.CashRegisterSession;
import com.volter.shared.specification.PredicateBuilder;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;

public final class CashRegisterSessionSpecification {

    private CashRegisterSessionSpecification() {}

    /** Restricts to sessions operated by one of the given staff members (empty set matches nothing). */
    public static Specification<CashRegisterSession> staffIdIn(Collection<Long> staffIds) {
        return (root, query, cb) -> staffIds.isEmpty() ? cb.disjunction() : root.get("staffId").in(staffIds);
    }

    /** Restricts to sessions operated by the given staff member. */
    public static Specification<CashRegisterSession> staffIdEquals(Long staffId) {
        return (root, query, cb) -> cb.equal(root.get("staffId"), staffId);
    }

    public static Specification<CashRegisterSession> matches(SessionFilterRequest filter) {
        return (root, query, cb) -> {
            var predicates = new PredicateBuilder<CashRegisterSession>(root, cb)
                    .withValue(root.get("cashRegister").get("id"), filter.cashRegisterId())
                    .withValue(root.get("staffId"), filter.staffId())
                    .withEnum(root.get("status"), filter.status())
                    .withRange(root.get("openedAt"), filter.openedFrom(), filter.openedTo())
                    .build();
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
