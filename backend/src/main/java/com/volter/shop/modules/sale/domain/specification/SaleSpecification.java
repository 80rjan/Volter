package com.volter.shop.modules.sale.domain.specification;

import com.volter.shop.modules.sale.application.dto.SaleFilterRequest;
import com.volter.shop.modules.sale.domain.model.Sale;
import com.volter.shared.specification.PredicateBuilder;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public final class SaleSpecification {

    private SaleSpecification() {}

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
                    .withRange(root.get("soldAt"), filter.soldFrom(), filter.soldTo())
                    .build();
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
