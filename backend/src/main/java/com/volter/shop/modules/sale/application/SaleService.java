package com.volter.shop.modules.sale.application;

import com.volter.shop.modules.cashregister.application.CashRegisterService;
import com.volter.shop.modules.cashregister.domain.model.CashRegisterSession;
import com.volter.shop.modules.customer.application.CustomerService;
import com.volter.shop.modules.customer.domain.model.Customer;
import com.volter.shop.modules.inventory.application.ItemService;
import com.volter.shop.modules.inventory.domain.model.Item;
import com.volter.shop.modules.sale.application.dto.SaleCreateRequest;
import com.volter.shop.modules.sale.application.dto.SaleFilterRequest;
import com.volter.shop.modules.sale.application.dto.SaleSellRequest;
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
import com.volter.shared.web.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * List sales with optional filters.
     */
    @Transactional(readOnly = true)
    public Page<Sale> list(SaleFilterRequest filter, Pageable pageable) {
        return saleRepository.findAll(SaleSpecification.matches(filter), pageable);
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
        saleTxRepository.save(SaleTransaction.record(tx, sale, SaleTransactionAction.SOLD));

        return sale;
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
