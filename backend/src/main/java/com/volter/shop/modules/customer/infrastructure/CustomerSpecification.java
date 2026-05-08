package com.volter.shop.modules.customer.infrastructure;

import com.volter.shop.modules.customer.application.dto.CustomerFilterDTO;
import com.volter.shop.modules.customer.domain.model.Customer;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

public class CustomerSpecification {

    public static Specification<Customer> withFilters(CustomerFilterDTO filters) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filters.getName() != null && !filters.getName().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("name")),
                        "%" + filters.getName().toLowerCase() + "%"));
            }

            if (filters.getPhoneNumber() != null && !filters.getPhoneNumber().isBlank()) {
                predicates.add(cb.like(root.get("phoneNumber"),
                        "%" + filters.getPhoneNumber() + "%"));
            }

            if (filters.getEmbg() != null && !filters.getEmbg().isBlank()) {
                predicates.add(cb.like(root.get("embg"),
                        filters.getEmbg() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
