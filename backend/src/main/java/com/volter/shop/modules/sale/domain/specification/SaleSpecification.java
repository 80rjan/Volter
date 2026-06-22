package com.volter.shop.modules.sale.domain.specification;

import com.volter.shop.modules.sale.application.dto.SaleFilterRequest;
import com.volter.shop.modules.sale.domain.model.Sale;
import com.volter.shared.specification.PredicateBuilder;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;

public final class SaleSpecification {

    private SaleSpecification() {}

    /** Restricts to sales created by one of the given staff members (empty set matches nothing). */
    public static Specification<Sale> createdByStaffIn(Collection<Long> staffIds) {
        return (root, query, cb) -> staffIds.isEmpty() ? cb.disjunction() : root.get("createdByStaffId").in(staffIds);
    }

    public static Specification<Sale> matches(SaleFilterRequest filter) {
        return (root, query, cb) -> {
            var predicates = new PredicateBuilder<Sale>(root, cb)
                    .withEnum(root.get("status"), filter.status())
                    .withStringLike(root.get("customer").get("fullName"), filter.customerFullName())
                    .withStringLike(root.get("customer").get("nationalId"), filter.customerNationalId())
                    .withAnyStringLike(filter.customerPhone(),
                            root.get("customer").get("phonePrimary"),
                            root.get("customer").get("phoneSecondary"))
                    .withEnum(root.get("item").get("type"), filter.itemType())
                    .withValue(root.get("createdByStaffId"), filter.createdByStaffId())
                    .withRange(root.get("soldAt"), filter.soldFrom(), filter.soldTo())
                    .build();
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
