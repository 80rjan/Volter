package com.volter.shop.modules.cashregister.domain.model;

import com.volter.shop.modules.cashregister.domain.model.enums.CashRegisterStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

/**
 * A physical cash drawer/terminal in the shop. Money flows through it during
 * {@link CashRegisterSession}s.
 */
@Entity
@Table(name = "cash_register")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CashRegister {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Cash register code is required")
    @Column(name = "code", nullable = false, length = 64)
    private String code;

    @NotNull(message = "Cash register status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    @Builder.Default
    private CashRegisterStatus status = CashRegisterStatus.ACTIVE;

    // When the register was retired (moved to INACTIVE); null while active.
    @Column(name = "deactivated_at")
    private OffsetDateTime deactivatedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    public static CashRegister create(String code) {
        return CashRegister.builder()
                .code(code)
                .status(CashRegisterStatus.ACTIVE)
                .build();
    }

    public boolean isActive() {
        return status == CashRegisterStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = CashRegisterStatus.INACTIVE;
        this.deactivatedAt = OffsetDateTime.now();
    }

    public void activate() {
        this.status = CashRegisterStatus.ACTIVE;
        this.deactivatedAt = null;
    }
}
