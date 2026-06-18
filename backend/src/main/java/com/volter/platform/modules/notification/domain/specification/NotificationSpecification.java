package com.volter.platform.modules.notification.domain.specification;

import com.volter.platform.modules.notification.application.dto.NotificationFilterRequest;
import com.volter.platform.modules.notification.domain.model.Notification;
import com.volter.shared.specification.PredicateBuilder;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public final class NotificationSpecification {

    private NotificationSpecification() {}

    public static Specification<Notification> forRecipient(Long staffId, NotificationFilterRequest filter) {
        return (root, query, cb) -> {
            var predicates = new PredicateBuilder<Notification>(root, cb)
                    .withValue(root.get("recipientStaffId"), staffId)
                    .withEnum(root.get("type"), filter.type())
                    .withValue(root.get("read"), filter.read())
                    .build();
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
