package com.volter.shop.modules.sale.domain.model;

import com.volter.shop.modules.cashregister.domain.model.CashRegisterSession;
import com.volter.shop.modules.customer.domain.model.Customer;
import com.volter.shop.modules.inventory.domain.model.Item;
import com.volter.shop.modules.sale.application.dto.dto.SaleFromForfeitedPawnDTO;
import com.volter.shop.modules.sale.web.request.SaleCreationRequest;
import com.volter.shop.modules.sale.application.dto.result.SaleSellResult;
import com.volter.shop.modules.sale.domain.model.enums.SaleStatus;
import com.volter.shop.modules.sale.domain.model.enums.SaleTransactionAction;
import com.volter.shop.modules.staff.domain.model.Staff;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionDirection;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionMarginType;
import com.volter.shop.shared.valueobject.Money;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
//@Table(
//        indexes = {
//                @Index(name = "idx_sale_status", columnList = "status"),
//
//                @Index(name = "idx_sale_active_created_at_desc", columnList = "active, createdAt DESC"),
//                @Index(name = "idx_sale_status_created_at_desc", columnList = "status, createdAt DESC"),
//                @Index(name = "idx_sale_customer_id_active", columnList = "customer_id, active")
//        },
//        uniqueConstraints = {
//                @UniqueConstraint(name = "uk_sale_item_active", columnNames = {"item_id", "active"})     // one active sale per item
//        }
//)
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull(message = "Sale purchase price is required")
    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "purchase_price", nullable = false))
    private Money purchasePrice;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "sold_price", nullable = true))
    private Money soldPrice;

    @NotNull(message = "Sale status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private SaleStatus status = SaleStatus.LISTED;

    @NotNull(message = "Sale active flag is required")
    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @NotNull(message = "Sale creation timestamp is required")
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @NotNull(message = "Sale update timestamp is required")
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @NotNull(message = "Sale customer is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false, foreignKey = @ForeignKey(name = "fk_sale_customer"))     // Customer.id
    private Customer customer;

    @NotNull(message = "Sale item is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id", nullable = false, foreignKey = @ForeignKey(name = "fk_sale_item"))     // Item.id
    private Item item;

    @Builder.Default
    @OneToMany(mappedBy = "sale", cascade = {CascadeType.PERSIST}, orphanRemoval = false)
    private List<SaleTransaction> transactions = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Transient
    public SaleTransaction getInitialTransaction() {
        return transactions.stream()
                .filter(t -> t.getAction() == SaleTransactionAction.CREATION || t.getAction() == SaleTransactionAction.ACQUISITION)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Initial creation transaction not found for sale id: " + id));
    }

    public SaleSellResult sell(Money soldPrice, String transactionDescription, CashRegisterSession cashRegisterSessions, Staff staff) {
        if (status != SaleStatus.LISTED) {
            throw new IllegalStateException("Only listed sales can be sold");
        }
        this.soldPrice = soldPrice;
        this.status = SaleStatus.SOLD;

        boolean underpaid = soldPrice.isLessThan(purchasePrice);

        SaleTransaction transaction = SaleTransaction.builder()
                .action(SaleTransactionAction.SALE)
                .amount(soldPrice)
                .direction(TransactionDirection.IN)
                .marginAmount(soldPrice.absoluteSubtract(purchasePrice))
                .marginType(underpaid ? TransactionMarginType.LOSS : TransactionMarginType.PROFIT)
                .description(transactionDescription)
                .cashRegisterSession(cashRegisterSessions)
                .staff(staff)
                .sale(this)
                .build();
        transactions.add(transaction);

        return new SaleSellResult(underpaid, transaction);
    }

    public static Sale create(SaleCreationRequest request, CashRegisterSession cashRegisterSessions, Staff staff, Item item, Customer customer) {
        Sale sale = Sale.builder()
                .purchasePrice(new Money(request.getPurchasePrice()))
                .customer(customer)
                .item(item)
                .build();

        SaleTransaction transaction = SaleTransaction.builder()
                .action(SaleTransactionAction.ACQUISITION)
                .amount(new Money(request.getPurchasePrice()))
                .direction(TransactionDirection.OUT)
                .marginAmount(new Money(0))
                .marginType(TransactionMarginType.NEUTRAL)
                .description(request.getTransactionDescription())
                .cashRegisterSession(cashRegisterSessions)
                .staff(staff)
                .sale(sale)
                .build();
        sale.transactions.add(transaction);

        return sale;
    }

    public static Sale moveFromPawn(SaleFromForfeitedPawnDTO saleDTO, CashRegisterSession cashRegisterSession, Staff staff, Item item, Customer customer) {
        Sale sale = Sale.builder()
                .purchasePrice(new Money(saleDTO.purchasePrice()))
                .customer(customer)
                .item(item)
                .build();

        SaleTransaction transaction = SaleTransaction.builder()
                .action(SaleTransactionAction.CREATION)
                .amount(new Money(0))
                .direction(TransactionDirection.NEUTRAL)
                .marginAmount(new Money(0))
                .marginType(TransactionMarginType.NEUTRAL)
                .description(null)
                .cashRegisterSession(cashRegisterSession)
                .staff(staff)
                .sale(sale)
                .build();
        sale.transactions.add(transaction);

        return sale;
    }
}
