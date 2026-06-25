package com.volter.shop.modules.sale.application;

import com.volter.shop.modules.cashregister.application.CashRegisterService;
import com.volter.shop.modules.cashregister.domain.model.CashRegisterSession;
import com.volter.shop.modules.customer.application.CustomerService;
import com.volter.shop.modules.customer.domain.model.Customer;
import com.volter.shop.modules.inventory.application.ItemService;
import com.volter.shop.modules.inventory.domain.model.Item;
import com.volter.shop.modules.inventory.domain.model.enums.ItemType;
import com.volter.shop.modules.sale.application.dto.SaleCreateRequest;
import com.volter.shop.modules.sale.application.dto.SaleFilterRequest;
import com.volter.shop.modules.sale.application.dto.SaleSellRequest;
import com.volter.shop.modules.sale.application.dto.SaleSummaryResponse;
import com.volter.shop.modules.sale.domain.model.Sale;
import com.volter.shop.modules.sale.domain.model.SaleTransaction;
import com.volter.shop.modules.sale.domain.model.enums.SaleTransactionAction;
import com.volter.shop.modules.sale.domain.repository.SaleRepository;
import com.volter.shop.modules.sale.domain.repository.SaleTransactionRepository;
import com.volter.shop.modules.sale.domain.specification.SaleSpecification;
import com.volter.shop.modules.transaction.application.TransactionService;
import com.volter.shop.modules.transaction.domain.model.Transaction;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionDirection;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionType;
import com.volter.shop.shared.valueobject.Money;
import com.volter.identity.modules.staff.application.StaffService;
import com.volter.platform.modules.notification.application.NotificationService;
import com.volter.platform.modules.notification.domain.model.enums.NotificationType;
import com.volter.shared.web.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SaleService {

    private final SaleRepository saleRepository;
    private final SaleTransactionRepository saleTxRepository;
    private final CustomerService customerService;
    private final ItemService itemService;
    private final CashRegisterService cashRegisterService;
    private final TransactionService transactionService;
    private final StaffService staffService;
    private final NotificationService notificationService;

    /**
     * List sales with optional filters. When filtering by the creating staff member,
     * the choice is restricted to the caller's own team (themselves + subordinates).
     */
    @Transactional(readOnly = true)
    public Page<Sale> list(SaleFilterRequest filter, Pageable pageable, Long callerStaffId) {
        Specification<Sale> spec = SaleSpecification.matches(filter);
        if (filter.createdByStaffId() != null) {
            List<Long> visible = new ArrayList<>(staffService.findSubordinateStaffIds(callerStaffId));
            visible.add(callerStaffId);
            spec = spec.and(SaleSpecification.createdByStaffIn(visible));
        }
        return saleRepository.findAll(spec, pageable);
    }

    /** Totals over the whole filtered set (the summary bar under the sales table). */
    @Transactional(readOnly = true)
    public SaleSummaryResponse summarize(SaleFilterRequest filter, Long callerStaffId) {
        Specification<Sale> spec = SaleSpecification.matches(filter);
        if (filter.createdByStaffId() != null) {
            List<Long> visible = new ArrayList<>(staffService.findSubordinateStaffIds(callerStaffId));
            visible.add(callerStaffId);
            spec = spec.and(SaleSpecification.createdByStaffIn(visible));
        }
        List<Sale> list = saleRepository.findAll(spec);
        long purchase = list.stream().mapToLong(s -> s.getPurchasePrice().amount()).sum();
        double goldGrams = list.stream()
                .filter(s -> s.getItem() != null && s.getItem().getType() == ItemType.GOLD)
                .mapToDouble(s -> goldWeightGrams(s.getItem()))
                .sum();
        return new SaleSummaryResponse(list.size(), purchase, goldGrams);
    }

    /** Read the gold weight (grams) from an item's free-form attributes; 0 if absent/unparseable. */
    private static double goldWeightGrams(Item item) {
        Object w = item.getAttributes() == null ? null : item.getAttributes().get("weightGrams");
        if (w instanceof Number n) return n.doubleValue();
        if (w != null) {
            try { return Double.parseDouble(w.toString()); } catch (NumberFormatException ignored) { /* fall through */ }
        }
        return 0;
    }

    /**
     * Get details of a specific sale by its ID.
     */
    @Transactional(readOnly = true)
    public Sale get(Long id) {
        return loadSale(id);
    }

    /**
     * Create a new item and sale listing for that item.
     * Record general transaction and sale transaction.
     * Record money flow from cash register session.
     */
    public Sale createListing(SaleCreateRequest request, Long staffId) {
        Customer customer = customerService.findOrFail(request.customerId());
        CashRegisterSession session = cashRegisterService.requireOpenSession(request.cashRegisterSessionId());

        // FUTURE: support listing an existing item (by id). For now a brand-new item
        // is always created inline as part of creating the sale listing.
        Item item = itemService.create(request.item());
        Money purchasePrice = new Money(request.purchasePrice());

        Sale sale = createListing(customer, item, purchasePrice, staffId);

        Transaction tx = transactionService.record(
                staffId, session, TransactionType.SALE, purchasePrice,
                TransactionDirection.OUT, "Item purchased for resale");
        saleTxRepository.save(SaleTransaction.record(tx, sale, SaleTransactionAction.LISTING_CREATED));

        cashRegisterService.applyTransaction(tx);

        return sale;
    }

    /**
     * Creates a sale listing for an item the shop owns (a forfeited pawn).
     * No transaction is recorded because the shop already owns the item and no money is exchanged.
     * The caller owns the item's status transition.
     */
    public Sale createListing(Customer customer, Item item, Money purchasePrice, Long staffId) {
        return saleRepository.save(Sale.create(customer, item, staffId, purchasePrice));
    }

    /**
     * Sell a listed item.
     * Mark item as sold.
     * Record general transaction and sale transaction.
     * Record money flow from cash register session.
     */
    public Sale sell(Long saleId, SaleSellRequest request, Long staffId) {
        Sale sale = loadSale(saleId);
        CashRegisterSession session = cashRegisterService.requireOpenSession(request.cashRegisterSessionId());

        Money salePrice = new Money(request.salePrice());
        sale.sell(salePrice);
        itemService.markSold(sale.getItem(), staffId);

        Transaction tx = transactionService.record(
                staffId, session, TransactionType.SALE, salePrice,
                TransactionDirection.IN, "Item sold");
        cashRegisterService.applyTransaction(tx);
        SaleTransaction saleTx = saleTxRepository.save(SaleTransaction.record(tx, sale, SaleTransactionAction.SOLD));

        // Risk flag: sold below what the shop paid for the item -> notify the manager.
        if (sale.isUnderwater()) {
            flagUnderpricedSale(staffId, sale, saleTx.getId());
        }

        return sale;
    }

    /**
     * Notify the staff member's manager about a sale closed below the item's
     * purchase price, pointing the risk flag at the created sale transaction.
     */
    private void flagUnderpricedSale(Long staffId, Sale sale, Long saleTransactionId) {
        staffService.findManagerId(staffId).ifPresent(managerId ->
                notificationService.create(
                        managerId,
                        NotificationType.RISK_FLAG,
                        "Продажба под откупна цена",
                        "Продажба #" + sale.getId() + " е затворена со " + sale.getSalePrice().amount()
                                + " ден., помалку од откупната цена од " + sale.getPurchasePrice().amount() + " ден.",
                        "sale_transaction",
                        saleTransactionId));
    }

    /**
     * Cancel a sale listing.
     */
    public Sale cancel(Long saleId) {
        // FUTURE: define what happens when a sale is canceled. For now we just mark it but the item stays in limbo state.
        Sale sale = loadSale(saleId);
        sale.cancel();
        return sale;
    }

    /**
     * Helper to load sale by id or throw 404
     */
    private Sale loadSale(Long id) {
        return saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found: " + id));
    }
}
