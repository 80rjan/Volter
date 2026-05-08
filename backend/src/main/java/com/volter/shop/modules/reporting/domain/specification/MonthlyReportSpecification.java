package com.volter.shop.modules.reporting.domain.specification;

import com.volter.shop.modules.reporting.domain.model.MonthlyReport_;
import com.volter.shop.modules.reporting.domain.model.MonthlyReport;
import com.volter.shop.modules.reporting.web.request.MonthlyReportFilterRequest;
import com.volter.shop.shared.specification.PredicateBuilder;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class MonthlyReportSpecification {

    public static Specification<MonthlyReport> withFilters(MonthlyReportFilterRequest filters) {
        return (root, query, cb) -> {
            var predicates = new PredicateBuilder<MonthlyReport>(root, cb)
                    .withValue(root.get(MonthlyReport_.month), filters.month())
                    .withValue(root.get(MonthlyReport_.year), filters.year())
                    .build();

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
