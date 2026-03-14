package com.volter.backend.cashRegister;

import com.volter.backend.transaction.Transaction;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class CashRegister {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull(message = "Cash register pawn count is required")
    @Column(nullable = false)
    private Integer pawnCount;

    @NotNull(message = "Cash register total pawn payout is required")
    @Column(nullable = false)
    private Integer totalPawnPayout;

    @NotNull(message = "Cash register total interest amount is required")
    @Column(nullable = false)
    private Integer totalInterestAmount;

    @NotNull(message = "Cash register sale count is required")
    @Column(nullable = false)
    private Integer saleCount;

    @NotNull(message = "Cash register total sale payout is required")
    @Column(nullable = false)
    private Integer totalSalePayout;

    @NotNull(message = "Cash register total gold weight in grams is required")
    @Column(nullable = false)
    private Float totalGoldWeightGrams;

    @NotNull(message = "Cash register balance is required")
    @Column(nullable = false)
    private Integer balance;

    @NotNull(message = "Cash register updated at is required")
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "cashRegister", cascade = CascadeType.PERSIST, orphanRemoval = false)
    private List<Transaction> transactions;

    @PrePersist
    @PreUpdate
    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

}
