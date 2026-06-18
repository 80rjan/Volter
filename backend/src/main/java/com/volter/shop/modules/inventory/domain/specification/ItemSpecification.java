package com.volter.shop.modules.inventory.domain.specification;

import com.volter.shop.modules.inventory.application.dto.ItemFilterRequest;
import com.volter.shop.modules.inventory.domain.model.Item;
import com.volter.shared.specification.PredicateBuilder;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public final class ItemSpecification {

    private ItemSpecification() {}

    public static Specification<Item> matches(ItemFilterRequest filter) {
        return (root, query, cb) -> {
            var predicates = new PredicateBuilder<Item>(root, cb)
                    .withEnum(root.get("type"), filter.type())
                    .withEnum(root.get("status"), filter.status())
                    .withEnum(root.get("origin"), filter.origin())
                    .build();
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
