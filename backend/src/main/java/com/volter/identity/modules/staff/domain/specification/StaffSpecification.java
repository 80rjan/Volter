package com.volter.identity.modules.staff.domain.specification;

import com.volter.identity.modules.staff.application.dto.StaffFilterRequest;
import com.volter.identity.modules.staff.domain.model.Staff;
import com.volter.shared.specification.PredicateBuilder;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public final class StaffSpecification {

    private StaffSpecification() {}

    public static Specification<Staff> matches(StaffFilterRequest filter) {
        return (root, query, cb) -> {
            var predicates = new PredicateBuilder<Staff>(root, cb)
                    .withStringLike(root.get("fullName"), filter.fullName())
                    .withStringLike(root.get("username"), filter.username())
                    .withEnum(root.get("status"), filter.status())
                    .build();
            if (filter.includeDeleted() == null || !filter.includeDeleted()) {
                predicates.add(cb.isNull(root.get("deletedAt")));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
