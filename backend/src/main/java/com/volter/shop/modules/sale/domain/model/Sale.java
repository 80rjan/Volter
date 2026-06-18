package com.volter.shop.modules.sale.domain.model;

import com.volter.shop.modules.customer.domain.model.Customer;
import com.volter.shop.modules.inventory.domain.model.Item;
import com.volter.shop.modules.sale.domain.model.enums.SaleStatus;
import com.volter.shop.shared.valueobject.Money;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

/**
 * A listing for selling an {@link Item} the shop owns (acquired through a
 * forfeit, trade-in or direct purchase). Transitions AVAILABLE → SOLD or
 * CANCELED. At most one AVAILABLE sale may exist per item (partial unique index).
 */
@Entity
@Table(name = "sale")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Customer is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false, foreignKey = @ForeignKey(name = "fk_sale_customer"))
    private Customer customer;

    @NotNull(message = "Item is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false, foreignKey = @ForeignKey(name = "fk_sale_item"))
    private Item item;

    @NotNull(message = "Staff is required")
    @Column(name = "created_by_staff_id", nullable = false)
    private Long createdByStaffId;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private SaleStatus status = SaleStatus.AVAILABLE;

    @NotNull(message = "Purchase price is required")
    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "purchase_price", nullable = false))
    private Money purchasePrice;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "sale_price"))
    private Money salePrice;

    @Column(name = "sold_at")
    private OffsetDateTime soldAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    public static Sale create(Customer customer, Item item, Long staffId, Money purchasePrice) {
        return Sale.builder()
                .customer(customer)
                .item(item)
                .createdByStaffId(staffId)
                .purchasePrice(purchasePrice)
                .status(SaleStatus.AVAILABLE)
                .build();
    }

    public void sell(Money salePrice) {
        if (status != SaleStatus.AVAILABLE) {
            throw new IllegalStateException("Only an AVAILABLE sale can be sold");
        }
        this.salePrice = salePrice;
        this.status = SaleStatus.SOLD;
        this.soldAt = OffsetDateTime.now();
    }

    public void cancel() {
        if (status != SaleStatus.AVAILABLE) {
            throw new IllegalStateException("Only an AVAILABLE sale can be canceled");
        }
        this.status = SaleStatus.CANCELED;
    }

    public boolean isAvailable() {
        return status == SaleStatus.AVAILABLE;
    }

    public boolean isUnderwater() {
        return salePrice != null && salePrice.isLessThan(purchasePrice);
    }

    /** Profit on a completed sale (sale price minus purchase price), or null if unsold. */
    public Integer profit() {
        if (salePrice == null) {
            return null;
        }
        return salePrice.amount() - purchasePrice.amount();
    }
}
