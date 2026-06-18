package com.volter.shop.modules.cashregister.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    public static CashRegister create(String code) {
        return CashRegister.builder()
                .code(code)
                .build();
    }
}
