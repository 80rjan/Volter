package com.volter.identity.modules.permission.domain.specification;

import com.volter.identity.modules.permission.application.dto.PermissionFilterRequest;
import com.volter.identity.modules.permission.domain.model.Permission;
import com.volter.shared.specification.PredicateBuilder;
import org.springframework.data.jpa.domain.Specification;

public final class PermissionSpecification {

    private PermissionSpecification() {}

    public static Specification<Permission> matches(PermissionFilterRequest filter) {
        return (root, query, cb) -> {
            var predicates = new PredicateBuilder<Permission>(root, cb)
                    .withStringLike(root.get("name"), filter.name())
                    .withEnum(root.get("category"), filter.category())
                    .build();
            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }
}
