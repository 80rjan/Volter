package com.volter.shop.modules.pawn.domain.specification;

import com.volter.shop.modules.pawn.application.dto.PawnFilterRequest;
import com.volter.shop.modules.pawn.domain.model.PawnContract;
import com.volter.shop.modules.pawn.domain.model.enums.PawnContractStatus;
import com.volter.shared.specification.PredicateBuilder;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public final class PawnContractSpecification {

    private PawnContractSpecification() {}

    public static Specification<PawnContract> matches(PawnFilterRequest filter) {
        return (root, query, cb) -> {
            var builder = new PredicateBuilder<PawnContract>(root, cb)
                    .withStringLike(root.get("customer").get("fullName"), filter.customerFullName())
                    .withStringLike(root.get("customer").get("nationalId"), filter.customerNationalId())
                    .withAnyStringLike(filter.customerPhone(),
                            root.get("customer").get("phonePrimary"),
                            root.get("customer").get("phoneSecondary"))
                    .withEnum(root.get("item").get("type"), filter.itemType())
                    .withEnum(root.get("status"), filter.status())
                    .withDateRange(root.get("issueDate"), filter.issuedFrom(), filter.issuedTo())
                    .withDateRange(root.get("dueDate"), filter.dueFrom(), filter.dueTo());
            var predicates = builder.build();
            if (Boolean.TRUE.equals(filter.overdueOnly())) {
                predicates.add(cb.equal(root.get("status"), PawnContractStatus.ACTIVE));
                predicates.add(cb.lessThan(root.get("dueDate"), LocalDate.now()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
