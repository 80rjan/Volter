package com.volter.platform.modules.shop.domain.specification;

import com.volter.platform.modules.shop.application.dto.ShopFilterRequest;
import com.volter.platform.modules.shop.domain.model.Shop;
import com.volter.shared.specification.PredicateBuilder;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public final class ShopSpecification {

    private ShopSpecification() {}

    public static Specification<Shop> matches(ShopFilterRequest filter) {
        return (root, query, cb) -> {
            var predicates = new PredicateBuilder<Shop>(root, cb)
                    .withStringLike(root.get("name"), filter.name())
                    .withStringLike(root.get("code"), filter.code())
                    .withEnum(root.get("status"), filter.status())
                    .build();
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
