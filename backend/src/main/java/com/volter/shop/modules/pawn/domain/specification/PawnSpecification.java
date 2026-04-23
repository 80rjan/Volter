package com.volter.shop.modules.pawn.domain.specification;

import com.volter.shop.modules.customer.domain.model.Customer_;
import com.volter.shop.modules.inventory.domain.model.Item_;
import com.volter.shop.modules.pawn.application.dto.filter.PawnFilter;
import com.volter.shop.modules.pawn.domain.model.Pawn;
import com.volter.shop.modules.pawn.domain.model.Pawn_;
import com.volter.shop.modules.pawn.domain.model.valueobject.PawnPeriod_;
import com.volter.shop.shared.specification.PredicateBuilder;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class PawnSpecification {

    public static Specification<Pawn> withFilters(PawnFilter filters) {
        return (root, query, cb) -> {

            var customerJoin = root.join(Pawn_.customer, JoinType.INNER);
            var itemJoin     = root.join(Pawn_.item, JoinType.INNER);
            var period       = root.get(Pawn_.period);

            var predicates = new PredicateBuilder<Pawn>(root, cb)
                    .withEnum(root.get(Pawn_.status), filters.status())
                    .withBoolean(root.get(Pawn_.active), filters.active())
                    .withDateRange(period.get(PawnPeriod_.maturityDate), filters.fromMaturityDate(), filters.toMaturityDate())
                    .withDateRange(period.get(PawnPeriod_.issueDate), filters.fromIssueDate(), filters.toIssueDate())
                    .withEnum(itemJoin.get(Item_.itemType), filters.itemType())
                    .withStringLike(customerJoin.get(Customer_.name), filters.customerName())
                    .withStringLike(customerJoin.get(Customer_.embg), filters.customerEmbg())
                    .withStringLike(customerJoin.get(Customer_.phoneNumber), filters.customerPhoneNumber())
                    .build();

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}

