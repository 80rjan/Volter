package com.volter.platform.modules.authevent.domain.model;

import com.volter.platform.modules.authevent.domain.model.enums.AuthEventType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

/**
 * Append-only audit record of authentication activity for a staff member
 * (identity context, referenced by id).
 */
@Entity
@Table(schema = "public", name = "auth_event")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AuthEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Staff is required")
    @Column(name = "staff_id", nullable = false)
    private Long staffId;

    @NotNull(message = "Event type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private AuthEventType type;

    @Column(name = "ip_address", length = 64)
    private String ipAddress;

    @Column(name = "user_agent", length = 512)
    private String userAgent;

    @CreationTimestamp
    @Column(name = "occurred_at", nullable = false)
    private OffsetDateTime occurredAt;

    public static AuthEvent record(Long staffId, AuthEventType type, String ipAddress, String userAgent) {
        return AuthEvent.builder()
                .staffId(staffId)
                .type(type)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();
    }

    public boolean isFailure() {
        return type == AuthEventType.LOGIN_FAILURE;
    }
}
