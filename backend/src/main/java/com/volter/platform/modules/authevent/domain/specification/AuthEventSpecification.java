package com.volter.platform.modules.authevent.domain.specification;

import com.volter.platform.modules.authevent.application.dto.AuthEventFilterRequest;
import com.volter.platform.modules.authevent.domain.model.AuthEvent;
import com.volter.shared.specification.PredicateBuilder;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;

public final class AuthEventSpecification {

    private AuthEventSpecification() {}

    /** Restricts to events of one of the given staff members (empty set matches nothing). */
    public static Specification<AuthEvent> staffIdIn(Collection<Long> staffIds) {
        return (root, query, cb) -> staffIds.isEmpty() ? cb.disjunction() : root.get("staffId").in(staffIds);
    }

    public static Specification<AuthEvent> matches(AuthEventFilterRequest filter) {
        return (root, query, cb) -> {
            var predicates = new PredicateBuilder<AuthEvent>(root, cb)
                    .withValue(root.get("staffId"), filter.staffId())
                    .withEnum(root.get("type"), filter.type())
                    .withRange(root.get("occurredAt"), filter.occurredFrom(), filter.occurredTo())
                    .build();
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
