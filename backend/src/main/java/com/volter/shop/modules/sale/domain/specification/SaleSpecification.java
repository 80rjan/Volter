package com.volter.shop.modules.sale.domain.specification;

import com.volter.shop.modules.customer.domain.model.Customer_;
import com.volter.shop.modules.inventory.domain.model.Item_;
import com.volter.shop.modules.sale.web.request.SaleFilterRequest;
import com.volter.shop.modules.sale.domain.model.Sale;
import com.volter.shop.modules.sale.domain.model.Sale_;
import com.volter.shop.shared.specification.PredicateBuilder;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class SaleSpecification {

    public static Specification<Sale> withFilters(SaleFilterRequest filters) {
        return (root, query, cb) -> {

            var customerJoin = root.join(Sale_.customer, JoinType.INNER);
            var itemJoin     = root.join(Sale_.item, JoinType.INNER);

            var predicates = new PredicateBuilder<Sale>(root, cb)
                    .withEnum(root.get(Sale_.status), filters.status())
                    .withValue(root.get(Sale_.active), filters.active())
                    .withEnum(itemJoin.get(Item_.itemType), filters.itemType())
                    .withStringLike(customerJoin.get(Customer_.name), filters.customerName())
                    .withStringLike(customerJoin.get(Customer_.embg), filters.customerEmbg())
                    .withStringLike(customerJoin.get(Customer_.phoneNumber), filters.customerPhoneNumber())
                    .build();

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
