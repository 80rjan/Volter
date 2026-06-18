package com.volter.identity.modules.role.domain.specification;

import com.volter.identity.modules.role.application.dto.RoleFilterRequest;
import com.volter.identity.modules.role.domain.model.Role;
import com.volter.shared.specification.PredicateBuilder;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public final class RoleSpecification {

    private RoleSpecification() {}

    public static Specification<Role> matches(RoleFilterRequest filter) {
        return (root, query, cb) -> {
            var predicates = new PredicateBuilder<Role>(root, cb)
                    .withStringLike(root.get("name"), filter.name())
                    .build();
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
