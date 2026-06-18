package com.volter.platform.modules.report.domain.specification;

import com.volter.platform.modules.report.application.dto.request.ReportFilterRequest;
import com.volter.platform.modules.report.domain.model.Report;
import com.volter.shared.specification.PredicateBuilder;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public final class ReportSpecification {

    private ReportSpecification() {}

    /** Restricts to system reports (no owner) and reports owned by the given staff member. */
    public static Specification<Report> visibleTo(Long staffId) {
        return (root, query, cb) -> cb.or(
                cb.isNull(root.get("ownerStaffId")),
                cb.equal(root.get("ownerStaffId"), staffId));
    }

    public static Specification<Report> matches(ReportFilterRequest filter) {
        return (root, query, cb) -> {
            var predicates = new PredicateBuilder<Report>(root, cb)
                    .withEnum(root.get("type"), filter.type())
                    .withValue(root.get("shopId"), filter.shopId())
                    .withValue(root.get("subjectStaffId"), filter.subjectStaffId())
                    .withDateRange(root.get("dateFrom"), filter.dateFrom(), filter.dateTo())
                    .build();
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
