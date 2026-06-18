package com.volter.shop.modules.pawn.application;

import com.volter.shop.modules.cashregister.application.CashRegisterService;
import com.volter.shop.modules.cashregister.domain.model.CashRegisterSession;
import com.volter.shop.modules.customer.application.CustomerService;
import com.volter.shop.modules.customer.domain.model.Customer;
import com.volter.shop.modules.inventory.application.ItemService;
import com.volter.shop.modules.inventory.domain.model.Item;
import com.volter.shop.modules.pawn.application.dto.*;
import com.volter.shop.modules.pawn.domain.model.PawnContract;
import com.volter.shop.modules.pawn.domain.model.PawnContractExtension;
import com.volter.shop.modules.pawn.domain.model.PawnTransaction;
import com.volter.shop.modules.pawn.domain.model.enums.PawnTransactionAction;
import com.volter.shop.modules.pawn.domain.repository.PawnContractExtensionRepository;
import com.volter.shop.modules.pawn.domain.repository.PawnContractRepository;
import com.volter.shop.modules.pawn.domain.repository.PawnTransactionRepository;
import com.volter.shop.modules.pawn.domain.specification.PawnContractSpecification;
import com.volter.shop.modules.sale.application.SaleService;
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

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PawnService {

    private final PawnContractRepository contractRepository;
    private final PawnContractExtensionRepository extensionRepository;
    private final PawnTransactionRepository pawnTxRepository;
    private final CustomerService customerService;
    private final ItemService itemService;
    private final CashRegisterService cashRegisterService;
    private final TransactionService transactionService;
    private final SaleService saleService;

    /**
     * List pawn contracts with optional filters.
     */
    @Transactional(readOnly = true)
    public Page<PawnContract> list(PawnFilterRequest filter, Pageable pageable) {
        return contractRepository.findAll(PawnContractSpecification.matches(filter), pageable);
    }

    /**
     * Get details of a specific pawn contract by its ID.
     */
    @Transactional(readOnly = true)
    public PawnContract get(Long id) {
        return loadContract(id);
    }

    /**
     * Create item and pawn contract.
     * Record general transaction and pawn transaction.
     * Record money flow from cash register session.
     */
    public PawnContract create(PawnCreateRequest request, Long staffId) {
        // FUTURE: support pawning an existing item (by id). For now a brand-new item
        // is always created inline as part of pawn creation.
        Item item = itemService.create(request.item());
        Customer customer = customerService.findOrFail(request.customerId());
        CashRegisterSession session = cashRegisterService.requireOpenSession(request.cashRegisterSessionId());

        Money principal = new Money(request.principalAmount());
        Money interest = new Money(request.interestAmount());

        PawnContract contract = PawnContract.create(
                customer, item, staffId, principal, interest, request.termDays(), request.issueDate());
        contractRepository.save(contract);

        Transaction tx = transactionService.record(
                staffId, session, TransactionType.PAWN, principal,
                TransactionDirection.OUT, "Pawn principal disbursed");
        pawnTxRepository.save(PawnTransaction.record(tx, contract, PawnTransactionAction.CONTRACT_CREATED));

        cashRegisterService.applyTransaction(tx);

        return contract;
    }

    /**
     * Extend pawn contract.
     * Record general transaction and pawn transaction.
     * Record money flow from cash register session.
     */
    public PawnContractExtension extend(Long contractId, PawnExtendRequest request, Long staffId) {
        PawnContract contract = loadContract(contractId);
        CashRegisterSession session = cashRegisterService.requireOpenSession(request.cashRegisterSessionId());

        Money interestPaid = new Money(request.interestPaid());
        Money fee = new Money(request.fee() == null ? 0 : request.fee());

        PawnContractExtension extension = contract.extend(interestPaid, fee);
        extensionRepository.save(extension);

        Money received = interestPaid.add(fee);
        Transaction tx = transactionService.record(
                staffId, session, TransactionType.PAWN, received,
                TransactionDirection.IN, "Pawn extension: interest + fee");
        pawnTxRepository.save(PawnTransaction.record(tx, contract, PawnTransactionAction.EXTENDED));

        cashRegisterService.applyTransaction(tx);

        return extension;
    }

    /**
     * Redeem pawn contract.
     * Mark item as redeemed.
     * Record general transaction and pawn transaction.
     * Record money flow from cash register session.
     */
    public PawnContract redeem(Long contractId, PawnRedeemRequest request, Long staffId) {
        PawnContract contract = loadContract(contractId);
        CashRegisterSession session = cashRegisterService.requireOpenSession(request.cashRegisterSessionId());

        contract.redeem();
        itemService.markRedeemed(contract.getItem(), staffId);

        Transaction tx = transactionService.record(
                staffId, session, TransactionType.PAWN, contract.totalDue(),
                TransactionDirection.IN, "Pawn redemption");
        pawnTxRepository.save(PawnTransaction.record(tx, contract, PawnTransactionAction.REDEEMED));

        cashRegisterService.applyTransaction(tx);

        return contract;
    }

    /**
     * Forfeit pawn contract.
     * Mark item as in sale and create a sale listing for it.
     */
    public PawnContract forfeit(Long contractId, Long staffId) {
        PawnContract contract = loadContract(contractId);
        contract.forfeit();
        itemService.markInSale(contract.getItem(), staffId);
        saleService.createListing(contract.getCustomer(), contract.getItem(), contract.getPrincipalAmount(), staffId);
        return contract;
    }

    // HELPERS

    /**
     * List pawn contracts that are due from today within the next given number of days.
     */
    public List<PawnContract> listDueInDays(int days) {
        return contractRepository.findAll(PawnContractSpecification.matches(
                PawnFilterRequest.builder()
                        .dueFrom(LocalDate.now())
                        .dueTo(LocalDate.now().plusDays(days))
                        .build()
        ));
    }

    /**
     * Helper to load contract by id or throw 404 if not found.
     */
    private PawnContract loadContract(Long id) {
        return contractRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pawn contract not found: " + id));
    }
}
