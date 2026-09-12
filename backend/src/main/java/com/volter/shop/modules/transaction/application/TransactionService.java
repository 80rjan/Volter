package com.volter.shop.modules.transaction.application;

import com.volter.identity.modules.staff.application.StaffService;
import com.volter.identity.modules.staff.infrastructure.mapper.StaffMapper;
import com.volter.shop.modules.cashregister.application.CashRegisterTxQueryService;
import com.volter.shop.modules.cashregister.domain.model.CashRegisterSession;
import com.volter.shop.modules.expense.application.ExpenseTxQueryService;
import com.volter.shop.modules.pawn.application.PawnTxQueryService;
import com.volter.shop.modules.sale.application.SaleTxQueryService;
import com.volter.shop.modules.transaction.application.dto.MonthlyProfitResponse;
import com.volter.shop.modules.transaction.application.dto.TransactionDetailedResponse;
import com.volter.shop.modules.transaction.application.dto.TransactionFilterRequest;
import com.volter.shop.modules.transaction.application.dto.TransactionResponse;
import com.volter.shop.modules.transaction.domain.model.ActivityEntry;
import com.volter.shop.modules.transaction.domain.model.Transaction;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionDirection;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionType;
import com.volter.shop.modules.transaction.domain.model.enums.ActivityType;
import com.volter.shop.modules.transaction.domain.repository.ActivityEntryRepository;
import com.volter.shop.modules.transaction.domain.repository.TransactionRepository;
import com.volter.shop.modules.transaction.domain.specification.ActivitySpecification;
import com.volter.shop.modules.transaction.infrastructure.mapper.TransactionMapper;
import com.volter.shop.shared.valueobject.Money;
import com.volter.shared.web.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
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
    private final ActivityEntryRepository activityRepository;
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

        Specification<ActivityEntry> spec = ActivitySpecification.matches(filter)
                .and(ActivitySpecification.staffIdIn(visibleStaffIds));

        // Filter by client name: gather matching ids from the pawn and sale modules
        // (their services own the customer link), then narrow by entry id. Ids are
        // prefixed per source, matching the activity_entry view.
        if (filter.clientName() != null && !filter.clientName().isBlank()) {
            Set<String> matching = new HashSet<>();
            pawnTxQueryService.findTransactionIdsByCustomerName(filter.clientName())
                    .forEach(id -> matching.add(transactionEntryId(id)));
            saleTxQueryService.findTransactionIdsByCustomerName(filter.clientName())
                    .forEach(id -> matching.add(transactionEntryId(id)));
            pawnTxQueryService.findEventIdsByCustomerName(filter.clientName())
                    .forEach(id -> matching.add(pawnEventEntryId(id)));
            spec = spec.and(ActivitySpecification.entryIdIn(matching));
        }

        Page<ActivityEntry> page = activityRepository.findAll(spec, withStableOrder(pageable));

        // Resolve the client column for the page, per source.
        List<Long> txIds = page.getContent().stream()
                .filter(ActivityEntry::isTransaction).map(ActivityEntry::getSourceId).toList();
        List<Long> eventIds = page.getContent().stream()
                .filter(e -> !e.isTransaction()).map(ActivityEntry::getSourceId).toList();

        Map<Long, String> txClientNames = new HashMap<>();
        txClientNames.putAll(pawnTxQueryService.findCustomerNamesByTransactionIds(txIds));
        txClientNames.putAll(saleTxQueryService.findCustomerNamesByTransactionIds(txIds));
        Map<Long, String> eventClientNames = pawnTxQueryService.findCustomerNamesByEventIds(eventIds);

        return page.map(entry -> transactionMapper.toResponse(entry,
                entry.isTransaction()
                        ? txClientNames.get(entry.getSourceId())
                        : eventClientNames.get(entry.getSourceId())));
    }

    /**
     * Appends a unique final sort key. Every column the page sorts by (date, type,
     * amount, direction) repeats across rows, and rows tied under a non-unique sort
     * come back in arbitrary order per query — with offset paging that repeats a row
     * on one page and skips it on another, so entries silently go missing. entryId
     * is unique across the merged list, which makes the order total.
     */
    private static Pageable withStableOrder(Pageable pageable) {
        if (pageable.isUnpaged() || pageable.getSort().getOrderFor("entryId") != null) {
            return pageable;
        }
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                pageable.getSort().and(Sort.by(Sort.Direction.ASC, "entryId")));
    }

    /** Entry ids in the activity_entry view are prefixed by source so they cannot collide. */
    private static String transactionEntryId(Long transactionId) {
        return "T" + transactionId;
    }

    private static String pawnEventEntryId(Long eventId) {
        return "E" + eventId;
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
                ActivityType.valueOf(tx.getType().name()),
                tx.getAmount() == null ? null : tx.getAmount().amount(),
                tx.getDirection(),
                tx.getDescription(),
                tx.getCreatedAt(),
                tx.getCashRegisterSession().getId(),
                staff, pawn, sale, expense, session);
    }

    /**
     * Full detailed view of a non-monetary pawn contract event. There is no ledger
     * row behind it, so amount, direction and session are null; the pawn block
     * carries the contract the event happened to. Visibility follows the same rule
     * as transactions: the caller's own, or one recorded by someone they manage.
     */
    public TransactionDetailedResponse getEventDetailed(Long eventId, Long callerStaffId) {
        var event = pawnTxQueryService.findEventById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Pawn contract event not found: " + eventId));
        if (!Objects.equals(event.performedByStaffId(), callerStaffId)
                && !staffService.isManagerOf(callerStaffId, event.performedByStaffId())) {
            throw new AccessDeniedException("Cannot access an event recorded by another staff member");
        }

        var staff = staffMapper.toResponse(staffService.get(event.performedByStaffId(), callerStaffId));
        var pawn = pawnTxQueryService.findDetailByEventId(eventId).orElse(null);

        // Mirrors the activity_entry view, which types these rows as 'PAWN_' || action.
        var type = ActivityType.valueOf("PAWN_" + event.action().name());

        return new TransactionDetailedResponse(
                event.id(), type, null, null, event.description(), event.occurredAt(),
                null, staff, pawn, null, null, null);
    }

    /** Total STAFF_BONUS a staff member has taken since the given moment (for the bonus ledger). */
    public long staffBonusTakenSince(Long staffId, OffsetDateTime since) {
        return transactionRepository.sumAmountByStaffAndTypeSince(staffId, TransactionType.STAFF_BONUS, since);
    }

    /** Total expenses across the whole shop since the given moment (for the bonus ledger). */
    public long shopExpensesSince(OffsetDateTime since) {
        return transactionRepository.sumAmountByTypeSince(TransactionType.EXPENSE, since);
    }

    /**
     * Shop profit from the first of the current month until today: pawn provision
     * (interest income) plus sale profit (margin), minus shop expenses over the
     * same period, giving the net profit. Provision and sale profit come from the
     * transaction ledger via each module's read-only query service (so this never
     * forms a service dependency cycle); expenses come from the transaction table.
     */
    public MonthlyProfitResponse monthlyProfit() {
        LocalDate today = LocalDate.now();
        LocalDate firstOfMonth = today.withDayOfMonth(1);
        OffsetDateTime since = firstOfMonth.atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime();
        long pawnProvision = pawnTxQueryService.provisionBetween(firstOfMonth, today);
        long saleProfit = saleTxQueryService.profitBetween(firstOfMonth, today);
        long totalExpenses = shopExpensesSince(since);
        long netProfit = pawnProvision + saleProfit - totalExpenses;
        return new MonthlyProfitResponse(pawnProvision, saleProfit, totalExpenses, netProfit);
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
