package com.volter.shop.modules.customer.domain.specification;

import com.volter.shop.modules.customer.application.dto.CustomerFilterRequest;
import com.volter.shop.modules.customer.domain.model.Customer;
import com.volter.shared.specification.PredicateBuilder;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public final class CustomerSpecification {

    private CustomerSpecification() {}

    public static Specification<Customer> matches(CustomerFilterRequest filter) {
        return (root, query, cb) -> {
            var predicates = new PredicateBuilder<Customer>(root, cb)
                    .withStringLike(root.get("fullName"), filter.fullName())
                    .withStringLike(root.get("nationalId"), filter.nationalId())
                    .withStringLike(root.get("phonePrimary"), filter.phone())
                    .withStringLike(root.get("city"), filter.city())
                    .build();
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
