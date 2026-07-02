package com.volter.shop.modules.transaction.application;

import com.volter.identity.modules.staff.application.StaffService;
import com.volter.identity.modules.staff.infrastructure.mapper.StaffMapper;
import com.volter.shop.modules.cashregister.application.CashRegisterTxQueryService;
import com.volter.shop.modules.cashregister.domain.model.CashRegisterSession;
import com.volter.shop.modules.expense.application.ExpenseTxQueryService;
import com.volter.shop.modules.pawn.application.PawnTxQueryService;
import com.volter.shop.modules.sale.application.SaleTxQueryService;
import com.volter.shop.modules.transaction.application.dto.TransactionDetailedResponse;
import com.volter.shop.modules.transaction.application.dto.TransactionFilterRequest;
import com.volter.shop.modules.transaction.application.dto.TransactionResponse;
import com.volter.shop.modules.transaction.domain.model.Transaction;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionDirection;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionType;
import com.volter.shop.modules.transaction.domain.repository.TransactionRepository;
import com.volter.shop.modules.transaction.domain.specification.TransactionSpecification;
import com.volter.shop.modules.transaction.infrastructure.mapper.TransactionMapper;
import com.volter.shop.shared.valueobject.Money;
import com.volter.shared.web.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final StaffService staffService;
    private final StaffMapper staffMapper;
    private final PawnTxQueryService pawnTxQueryService;
    private final SaleTxQueryService saleTxQueryService;
    private final ExpenseTxQueryService expenseTxQueryService;
    private final CashRegisterTxQueryService cashRegisterTxQueryService;

    /**
     * Transactions the caller may see: their own plus those of every staff member
     * below them in the management tree (recursive), narrowed by the filter. Each
     * row is enriched with the client name resolved through its pawn/sale subtype
     * (null for expense / cash-register transactions, which have no client).
     */
    public Page<TransactionResponse> list(TransactionFilterRequest filter, Pageable pageable, Long staffId) {
        List<Long> visibleStaffIds = new ArrayList<>(staffService.findSubordinateStaffIds(staffId));
        visibleStaffIds.add(staffId);

        Specification<Transaction> spec = TransactionSpecification.matches(filter)
                .and(TransactionSpecification.staffIdIn(visibleStaffIds));

        // Filter by client name: gather matching transaction ids from the pawn and
        // sale modules (their services own the customer link), then narrow by id.
        if (filter.clientName() != null && !filter.clientName().isBlank()) {
            Set<Long> matchingTxIds = new HashSet<>(pawnTxQueryService.findTransactionIdsByCustomerName(filter.clientName()));
            matchingTxIds.addAll(saleTxQueryService.findTransactionIdsByCustomerName(filter.clientName()));
            spec = spec.and(TransactionSpecification.idIn(matchingTxIds));
        }

        Page<Transaction> page = transactionRepository.findAll(spec, pageable);

        List<Long> pageTxIds = page.getContent().stream().map(Transaction::getId).toList();
        Map<Long, String> clientNames = new HashMap<>();
        clientNames.putAll(pawnTxQueryService.findCustomerNamesByTransactionIds(pageTxIds));
        clientNames.putAll(saleTxQueryService.findCustomerNamesByTransactionIds(pageTxIds));

        return page.map(tx -> transactionMapper.toResponse(tx, clientNames.get(tx.getId())));
    }

    /**
     * A single transaction, only if it is the caller's own or was made by someone they manage.
     */
    public Transaction get(Long id, Long staffId) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found: " + id));
        if (!Objects.equals(transaction.getStaffId(), staffId) && !staffService.isManagerOf(staffId, transaction.getStaffId())) {
            throw new AccessDeniedException("Cannot access a transaction made by another staff member");
        }
        return transaction;
    }

    /**
     * Full detailed view of a transaction: ledger data, the staff member who made
     * it, and the one type-specific block (pawn / sale / expense / cash register
     * session) that matches its type. Access is enforced via {@link #get}; once a
     * transaction is visible to the caller, so are its sub-details.
     */
    public TransactionDetailedResponse getDetailed(Long id, Long callerStaffId) {
        Transaction tx = get(id, callerStaffId);

        var staff = staffMapper.toResponse(staffService.get(tx.getStaffId(), callerStaffId));

        var pawn = tx.getType() == TransactionType.PAWN
                ? pawnTxQueryService.findDetailByTransactionId(id).orElse(null) : null;
        var sale = tx.getType() == TransactionType.SALE
                ? saleTxQueryService.findDetailByTransactionId(id).orElse(null) : null;
        var expense = tx.getType() == TransactionType.EXPENSE
                ? expenseTxQueryService.findDetailByTransactionId(id).orElse(null) : null;
        // Every transaction belongs to a cash register session — show it for all types.
        var session = cashRegisterTxQueryService.findSessionDetail(tx.getCashRegisterSession().getId()).orElse(null);

        return new TransactionDetailedResponse(
                tx.getId(),
                tx.getType(),
                tx.getAmount() == null ? null : tx.getAmount().amount(),
                tx.getDirection(),
                tx.getDescription(),
                tx.getCreatedAt(),
                tx.getCashRegisterSession().getId(),
                staff, pawn, sale, expense, session);
    }

    /** Total STAFF_BONUS a staff member has taken since the given moment (for the bonus ledger). */
    public long staffBonusTakenSince(Long staffId, OffsetDateTime since) {
        return transactionRepository.sumAmountByStaffAndTypeSince(staffId, TransactionType.STAFF_BONUS, since);
    }

    /** Total expenses across the whole shop since the given moment (for the bonus ledger). */
    public long shopExpensesSince(OffsetDateTime since) {
        return transactionRepository.sumAmountByTypeSince(TransactionType.EXPENSE, since);
    }

    // CROSS MODULE OPERATIONS
    /**
     * The transactions module write entry point.
     * This is for recording transactions from other modules, instead of letting them touch the repository directly.
     */
    @Transactional
    public Transaction record(Long staffId, CashRegisterSession session, TransactionType type,
                              Money amount, TransactionDirection direction, String description) {
        return transactionRepository.save(
                Transaction.create(staffId, session, type, amount, direction, description));
    }
}
