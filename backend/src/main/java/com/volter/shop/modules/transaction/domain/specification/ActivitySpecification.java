package com.volter.shop.modules.transaction.domain.specification;

import com.volter.shop.modules.transaction.application.dto.TransactionFilterRequest;
import com.volter.shop.modules.transaction.domain.model.ActivityEntry;
import com.volter.shared.specification.PredicateBuilder;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;

public final class ActivitySpecification {

    private ActivitySpecification() {}

    /** Restricts to entries made by one of the given staff members (empty set matches nothing). */
    public static Specification<ActivityEntry> staffIdIn(Collection<Long> staffIds) {
        return (root, query, cb) -> staffIds.isEmpty() ? cb.disjunction() : root.get("staffId").in(staffIds);
    }

    /** Restricts to the given entry ids, e.g. {@code T12}/{@code E3} (empty set matches nothing). */
    public static Specification<ActivityEntry> entryIdIn(Collection<String> entryIds) {
        return (root, query, cb) -> entryIds.isEmpty() ? cb.disjunction() : root.get("entryId").in(entryIds);
    }

    public static Specification<ActivityEntry> matches(TransactionFilterRequest filter) {
        return (root, query, cb) -> {
            var predicates = new PredicateBuilder<ActivityEntry>(root, cb)
                    .withEnum(root.get("type"), filter.type())
                    .withEnum(root.get("direction"), filter.direction())
                    .withValue(root.get("staffId"), filter.staffId())
                    .withRange(root.get("createdAt"), filter.createdFrom(), filter.createdTo())
                    .build();
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
