package com.volter.platform.modules.report.domain.specification;

import com.volter.platform.modules.report.application.dto.request.ReportFilterRequest;
import com.volter.platform.modules.report.domain.model.Report;
import com.volter.shared.specification.PredicateBuilder;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;

public final class ReportSpecification {

    private ReportSpecification() {}

    /**
     * Restricts to reports the caller may see: system reports (no owner, e.g. the
     * monthly shop summary) and staff-performance reports about one of the given
     * staff members (the caller plus their subordinates).
     */
    public static Specification<Report> visibleTo(Collection<Long> visibleStaffIds) {
        return (root, query, cb) -> cb.or(
                cb.isNull(root.get("ownerStaffId")),
                root.get("subjectStaffId").in(visibleStaffIds));
    }

    /** Restricts to reports belonging to the given shop (reports live in the shared public schema). */
    public static Specification<Report> forShop(Long shopId) {
        return (root, query, cb) -> cb.equal(root.get("shopId"), shopId);
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
