package com.volter.shop.modules.cashregister.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CashRegister {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull(message = "Cash register code is required")
    @Column(nullable = false)
    private String code;

    @NotNull(message = "Cash register created at is required")
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "cashRegister", fetch = FetchType.LAZY)
    private List<CashRegisterSession> sessions;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

}
