package com.volter.shop.modules.pawn.application;

import com.volter.shop.modules.cashregister.application.CashRegisterService;
import com.volter.shop.modules.cashregister.domain.model.CashRegisterSession;
import com.volter.shop.modules.customer.application.CustomerService;
import com.volter.shop.modules.customer.domain.model.Customer;
import com.volter.shop.modules.inventory.application.ItemService;
import com.volter.shop.modules.inventory.domain.model.Item;
import com.volter.shop.modules.inventory.domain.model.enums.ItemType;
import com.volter.shop.modules.pawn.application.dto.*;
import com.volter.shop.modules.pawn.domain.model.PawnContract;
import com.volter.shop.modules.pawn.domain.model.PawnContractExtension;
import com.volter.shop.modules.pawn.domain.model.PawnContractEvent;
import com.volter.shop.modules.pawn.domain.model.PawnContractNote;
import com.volter.shop.modules.pawn.domain.model.PawnTransaction;
import com.volter.shop.modules.pawn.domain.model.enums.PawnContractEventAction;
import com.volter.shop.modules.pawn.domain.model.enums.PawnContractNoteStatus;
import com.volter.shop.modules.pawn.domain.model.enums.PawnTransactionAction;
import com.volter.shop.modules.pawn.domain.repository.PawnContractExtensionRepository;
import com.volter.shop.modules.pawn.domain.repository.PawnContractRepository;
import com.volter.shop.modules.pawn.domain.repository.PawnContractEventRepository;
import com.volter.shop.modules.pawn.domain.repository.PawnContractNoteRepository;
import com.volter.shop.modules.pawn.domain.repository.PawnTransactionRepository;
import com.volter.shop.modules.pawn.domain.specification.PawnContractSpecification;
import com.volter.shop.modules.sale.application.SaleService;
import com.volter.shop.modules.transaction.application.TransactionService;
import com.volter.shop.modules.transaction.domain.model.Transaction;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionDirection;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionType;
import com.volter.shop.shared.valueobject.Money;
import com.volter.identity.modules.staff.application.StaffService;
import com.volter.shop.modules.notification.application.NotificationService;
import com.volter.shop.modules.notification.domain.model.enums.NotificationType;
import com.volter.shared.web.exception.BusinessRuleException;
import com.volter.shared.web.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class PawnService {

    private final PawnContractRepository contractRepository;
    private final PawnContractExtensionRepository extensionRepository;
    private final PawnContractNoteRepository noteRepository;
    private final PawnContractEventRepository eventRepository;
    private final PawnTransactionRepository pawnTxRepository;
    private final CustomerService customerService;
    private final ItemService itemService;
    private final CashRegisterService cashRegisterService;
    private final TransactionService transactionService;
    private final SaleService saleService;
    private final StaffService staffService;
    private final NotificationService notificationService;

    /**
     * List pawn contracts with optional filters. When filtering by the opening staff
     * member, the choice is restricted to the caller's own team (themselves + their
     * subordinates), so a manager can only narrow to staff below them.
     */
    @Transactional(readOnly = true)
    public Page<PawnContract> list(PawnFilterRequest filter, Pageable pageable, Long callerStaffId) {
        Specification<PawnContract> spec = PawnContractSpecification.matches(filter);
        if (filter.createdByStaffId() != null) {
            spec = spec.and(PawnContractSpecification.createdByStaffIn(visibleStaffIds(callerStaffId)));
        }
        return contractRepository.findAll(spec, pageable);
    }

    private List<Long> visibleStaffIds(Long callerStaffId) {
        List<Long> ids = new ArrayList<>(staffService.findSubordinateStaffIds(callerStaffId));
        ids.add(callerStaffId);
        return ids;
    }

    /** Totals over the whole filtered set (the summary bar under the pawns table). */
    @Transactional(readOnly = true)
    public PawnSummaryResponse summarize(PawnFilterRequest filter, Long callerStaffId) {
        Specification<PawnContract> spec = PawnContractSpecification.matches(filter);
        if (filter.createdByStaffId() != null) {
            spec = spec.and(PawnContractSpecification.createdByStaffIn(visibleStaffIds(callerStaffId)));
        }
        List<PawnContract> list = contractRepository.findAll(spec);
        long principal = list.stream().mapToLong(p -> p.getPrincipalAmount().amount()).sum();
        long interest = list.stream().mapToLong(p -> p.getInterestAmount().amount()).sum();
        double goldGrams = list.stream()
                .filter(p -> p.getItem() != null && p.getItem().getType() == ItemType.GOLD)
                .mapToDouble(p -> goldWeightGrams(p.getItem()))
                .sum();

        // Month-scoped figures (over the same filtered set, so they respect the active filters).
        LocalDate today = LocalDate.now();
        LocalDate firstOfMonth = today.withDayOfMonth(1);
        LocalDate endOfMonth = today.withDayOfMonth(today.lengthOfMonth());
        // Principal handed out for pawns opened this month (by issue date).
        long monthlyPrincipal = list.stream()
                .filter(p -> p.getIssueDate() != null
                        && !p.getIssueDate().isBefore(firstOfMonth)
                        && !p.getIssueDate().isAfter(endOfMonth))
                .mapToLong(p -> p.getPrincipalAmount().amount())
                .sum();
        // Interest still to collect on pawns that expire by the end of this month
        // (due date on/before month end — includes overdue-but-still-open contracts).
        long monthlyInterest = list.stream()
                .filter(p -> p.getDueDate() != null && !p.getDueDate().isAfter(endOfMonth))
                .mapToLong(p -> p.getInterestAmount().amount())
                .sum();

        return new PawnSummaryResponse(list.size(), principal, monthlyPrincipal, interest, monthlyInterest, goldGrams);
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
                TransactionDirection.OUT, "Креирање на нов договор");
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
                TransactionDirection.IN, "Продолжување на договор");
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

        // The amount the staff member collected is the final redemption price.
        Money paid = new Money(request.paidAmount());
        Transaction tx = transactionService.record(
                staffId, session, TransactionType.PAWN, paid,
                TransactionDirection.IN, "Затворање на договор");
        PawnTransaction pawnTx = pawnTxRepository.save(
                PawnTransaction.record(tx, contract, PawnTransactionAction.REDEEMED));

        cashRegisterService.applyTransaction(tx);

        // Risk flag: if less than expected (principal + interest, plus a late
        // penalty when overdue) was collected, notify the staff member's manager.
        Money expected = contract.expectedRedemptionAmount();
        if (paid.isLessThan(expected)) {
            flagUnderpaidRedemption(staffId, contract, paid, expected, pawnTx.getId());
        }

        return contract;
    }

    /**
     * Edit the terms of an ACTIVE pawn contract. Interest and term are applied
     * directly (term shifts the due date); changing the principal moves cash and
     * is recorded as an ADJUSTED pawn transaction against an open session:
     * raising it pays the customer the difference (OUT), lowering it takes it back (IN).
     */
    public PawnContract updateContract(Long contractId, PawnContractUpdateRequest request, Long staffId) {
        PawnContract contract = loadContract(contractId);
        if (!contract.isActive()) {
            throw new BusinessRuleException("Only active pawn contracts can be edited");
        }

        int delta = request.principalAmount() - contract.getPrincipalAmount().amount();
        if (delta != 0) {
            if (request.cashRegisterSessionId() == null) {
                throw new BusinessRuleException("An open cash register session is required to change the principal amount");
            }
            CashRegisterSession session = cashRegisterService.requireOpenSession(request.cashRegisterSessionId());

            // delta > 0: more cash handed out (OUT); delta < 0: cash returned (IN).
            TransactionDirection direction = delta > 0 ? TransactionDirection.OUT : TransactionDirection.IN;
            String note = delta > 0 ? "Зголемена главница на залог" : "Намалена главница на залог";
            Transaction tx = transactionService.record(
                    staffId, session, TransactionType.PAWN, new Money(Math.abs(delta)), direction, note);
            pawnTxRepository.save(PawnTransaction.record(tx, contract, PawnTransactionAction.ADJUSTED));
            cashRegisterService.applyTransaction(tx);
        }

        contract.updateTerms(new Money(request.principalAmount()), new Money(request.interestAmount()),
                request.termDays(), request.issueDate());

        notifyManagerOfUpdate(staffId, contract);
        return contract;
    }

    /** Notify the updating staff member's manager (if any) that a pawn contract was edited. */
    private void notifyManagerOfUpdate(Long staffId, PawnContract contract) {
        staffService.findManagerId(staffId).ifPresent(managerId -> {
            String staffName = staffService.findStaffNames(Set.of(staffId)).get(staffId);
            notificationService.create(
                    managerId,
                    NotificationType.PAWN_UPDATED,
                    "Ажуриран залог",
                    (staffName != null ? staffName : "Вработен") + " го ажурираше залог #" + contract.getId() + ".",
                    "pawn_contract",
                    contract.getId());
        });
    }

    /**
     * Notify the staff member's manager about an underpaid redemption, pointing
     * the risk flag at the created pawn transaction.
     */
    private void flagUnderpaidRedemption(Long staffId, PawnContract contract,
                                         Money paid, Money expected, Long pawnTransactionId) {
        staffService.findManagerId(staffId).ifPresent(managerId ->
                notificationService.create(
                        managerId,
                        NotificationType.RISK_FLAG,
                        "Намалена исплата при затворање залог",
                        "Залог #" + contract.getId() + " е затворен со " + paid.amount()
                                + " ден., помалку од очекуваните " + expected.amount() + " ден.",
                        "pawn_transaction",
                        pawnTransactionId));
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
        // No money changes hands here, so there is no ledger transaction to show
        // staff that this happened. Record it as a contract event instead; the
        // activity list surfaces it next to the transactions.
        eventRepository.save(PawnContractEvent.record(
                contract, PawnContractEventAction.FORFEITED, staffId, "Залогот е пренесен во продажба"));
        return contract;
    }

    // NOTES

    /**
     * Notes of a contract, newest first. Loaded separately from the contract so the
     * detailed response can carry both notes and extensions without fetching two
     * List associations in a single query.
     */
    @Transactional(readOnly = true)
    public List<PawnContractNote> listNotes(Long contractId) {
        requireContractExists(contractId);
        return noteRepository.findByPawnContractIdOrderByCreatedAtDesc(contractId);
    }

    /**
     * Attach a note to a contract. Allowed on a contract in any status — a note
     * records something about the pawn, not a change to its terms.
     */
    public PawnContractNote addNote(Long contractId, PawnContractNoteCreateRequest request, Long staffId) {
        PawnContract contract = loadContract(contractId);
        return noteRepository.save(PawnContractNote.of(contract, staffId, request.description().trim()));
    }

    /** Move a note between ACTIVE and RESOLVED. */
    public PawnContractNote updateNoteStatus(Long contractId, Long noteId, PawnContractNoteStatus status) {
        PawnContractNote note = noteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException("Pawn note not found: " + noteId));
        // The note id is global, so reject one that belongs to a different contract
        // rather than silently updating it through the wrong URL.
        if (!note.getPawnContract().getId().equals(contractId)) {
            throw new BusinessRuleException("Note " + noteId + " does not belong to pawn contract " + contractId);
        }
        if (status == PawnContractNoteStatus.RESOLVED) {
            note.resolve();
        } else {
            note.reopen();
        }
        return note;
    }

    // HELPERS

    /**
     * Helper to load contract by id or throw 404 if not found.
     */
    private PawnContract loadContract(Long id) {
        return contractRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pawn contract not found: " + id));
    }

    /** 404 for an unknown contract, without the entity-graph load loadContract does. */
    private void requireContractExists(Long id) {
        if (!contractRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pawn contract not found: " + id);
        }
    }
}
