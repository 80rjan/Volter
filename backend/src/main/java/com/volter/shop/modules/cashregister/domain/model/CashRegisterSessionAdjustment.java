package com.volter.shop.modules.cashregister.domain.model;

import com.volter.shop.modules.staff.domain.model.Staff;
import com.volter.shop.shared.valueobject.Money;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CashRegisterSessionAdjustment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private Money amount;

    private String note;

    private LocalDateTime createdAt;

    @NotNull(message = "Cash register session is required")
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cash_register_session_id", nullable = false, foreignKey = @ForeignKey(name = "fk_cash_register_session_adjustment_cash_register_session"))
    private CashRegisterSession cashRegisterSession;

    @NotNull(message = "Created by staff is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by_staff_id", nullable = false, foreignKey = @ForeignKey(name = "fk_cash_register_session_adjustment_created_by_staff"))
    private Staff createdBy;
}
