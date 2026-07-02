package com.volter.shop.modules.cashregister.application;

import com.volter.shop.modules.cashregister.application.dto.*;
import com.volter.shop.modules.cashregister.domain.model.*;
import com.volter.shop.modules.cashregister.domain.model.enums.CashRegisterSessionStatus;
import com.volter.shop.modules.cashregister.domain.model.enums.CashRegisterStatus;
import com.volter.shop.modules.cashregister.domain.repository.*;
import com.volter.identity.modules.staff.application.StaffService;
import com.volter.shop.modules.notification.application.NotificationService;
import com.volter.shop.modules.notification.domain.model.enums.NotificationType;
import com.volter.shop.modules.cashregister.domain.specification.CashRegisterSessionSpecification;
import com.volter.shop.modules.cashregister.domain.specification.DiscrepancySpecification;
import com.volter.shop.modules.pawn.application.PawnDueQueryService;
import com.volter.shop.modules.transaction.application.TransactionService;
import com.volter.shop.modules.transaction.domain.model.Transaction;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionType;
import com.volter.shop.shared.valueobject.Money;
import com.volter.shared.web.exception.BusinessRuleException;
import com.volter.shared.web.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class CashRegisterService {

    private final CashRegisterRepository cashRegisterRepository;
    private final CashRegisterSessionRepository sessionRepository;
    private final CashRegisterTransactionRepository cashTxRepository;
    private final CashRegisterSessionDiscrepancyRepository discrepancyRepository;
    private final TransactionService transactionService;
    private final PawnDueQueryService pawnDueQueryService;
    private final StaffService staffService;
    private final NotificationService notificationService;

    // ----- registers -----

    /**
     * List the active cash registers. Retired (INACTIVE) registers are hidden but
     * kept in the database so their historical sessions/transactions remain intact.
     */
    @Transactional(readOnly = true)
    public List<CashRegister> listRegisters() {
        return cashRegisterRepository.findAllByStatus(CashRegisterStatus.ACTIVE);
    }

    /**
     * Create a new cash register, if code doesn't exist.
     */
    public CashRegister createRegister(CashRegisterCreateRequest request) {
        if (cashRegisterRepository.existsByCode(request.code())) {
            throw new BusinessRuleException("Cash register code already exists: " + request.code());
        }
        return cashRegisterRepository.save(CashRegister.create(request.code()));
    }

    // ----- sessions -----

    /**
     * List sessions operated by staff members below the manager in the management tree
     * (recursive), narrowed by the filter.
     */
    @Transactional(readOnly = true)
    public Page<CashRegisterSession> listSessions(Long managerStaffId, SessionFilterRequest filter, Pageable pageable) {
        List<Long> staffIds = staffService.findSubordinateStaffIds(managerStaffId);
        staffIds.add(managerStaffId); // include own sessions as well
        return sessionRepository.findAll(
                CashRegisterSessionSpecification.matches(filter)
                        .and(CashRegisterSessionSpecification.staffIdIn(staffIds)),
                pageable);
    }

    /**
     * Get a cash register session by ID, throwing if not found.
     * Only allow the staff member that operates the session to fetch it, or its manager in the management tree.
     */
    @Transactional(readOnly = true)
    public CashRegisterSession getSession(Long id, Long staffId) {
        CashRegisterSession session = loadSession(id);
        if (!Objects.equals(session.getStaffId(), staffId) && !staffService.isManagerOf(staffId, session.getStaffId())) {
            throw new AccessDeniedException("Cannot access session operated by another staff member");
        }

        return session;
    }

    /**
     * Open a new cash register session for the given register.
     * A register can only have one OPEN session at a time.
     * Expected interest is calculated as the total interest amount of all pawn contracts due today.
     */
    public CashRegisterSession openSession(Long registerId, SessionOpenRequest request, Long staffId) {
        CashRegister register = loadRegister(registerId);
        sessionRepository.findByCashRegister_IdAndStatus(registerId, CashRegisterSessionStatus.OPEN)
                .ifPresent(s -> { throw new BusinessRuleException("Cash register with id " + registerId + " already has an OPEN session"); });

        Money expectedInterest = pawnDueQueryService.totalInterestDue(0);

        CashRegisterSession session = sessionRepository.save(
                CashRegisterSession.open(register, staffId, request.openingBalance(), expectedInterest));

        flagOpeningDiscrepancy(register, session, request.openingBalance(), staffId);
        return session;
    }

    /**
     * If the new opening balance doesn't match the previous session's counted
     * closing balance for this register (cash changed while the drawer was closed),
     * record an OPENING discrepancy and notify the opener's manager.
     */
    private void flagOpeningDiscrepancy(CashRegister register, CashRegisterSession session, int openingBalance, Long staffId) {
        sessionRepository.findFirstByCashRegister_IdAndStatusOrderByClosedAtDesc(register.getId(), CashRegisterSessionStatus.CLOSED)
                .filter(prev -> prev.getClosingBalance() != null && prev.getClosingBalance() != openingBalance)
                .ifPresent(prev -> {
                    int prevClosing = prev.getClosingBalance();
                    discrepancyRepository.save(CashRegisterSessionDiscrepancy.ofOpening(session, prevClosing, openingBalance));
                    int diff = openingBalance - prevClosing;
                    staffService.findManagerId(staffId).ifPresent(managerId ->
                            notificationService.create(
                                    managerId,
                                    NotificationType.CASH_REGISTER_SESSION_DISCREPANCY,
                                    "Несовпаѓање при отворање каса",
                                    "Касата " + register.getCode() + " е отворена со " + openingBalance
                                            + " ден., но последното затворање беше " + prevClosing
                                            + " ден. (разлика " + diff + " ден.).",
                                    null, null));
                });
    }

    /**
     * Close a cash register session for the given cash register.
     * A register can have at most one OPEN session, which is the one to be closed.
     * It can be closed by the staff member that opened it OR by a manager of that
     * staff member (direct or indirect).
     * Closing balance is counted by the caller and provided in the request.
     * If there is discrepancy between the counted closing balance and the expected, a discrepancy record is created and linked to the session.
     */
    public CashRegisterSession closeSession(Long registerId, SessionCloseRequest request, Long staffId) {
        CashRegisterSession session = sessionRepository.findByCashRegister_IdAndStatus(registerId, CashRegisterSessionStatus.OPEN)
                .orElseThrow(() -> new BusinessRuleException("No OPEN session found for cash register with id " + registerId));

        if (!Objects.equals(session.getStaffId(), staffId) && !staffService.isManagerOf(staffId, session.getStaffId())) {
            throw new AccessDeniedException("Only the operator of the session or their manager can close it");
        }

        CashRegisterSessionDiscrepancy discrepancy = session.close(request.countedClosingBalance());
        if (discrepancy != null) {
            discrepancyRepository.save(discrepancy);
        }
        return session;
    }

    // ----- manual cash register transactions -----

    /**
     * Record a cash register transaction (cash inflow or outflow) for a session.
     * Cash register session is allowed to have negative balance, so no checks are made for that.
     * Transactions cannot be recorded on a CLOSED session.
     * The parent transaction is recorded, as well as the cash register transaction linked to it.
     */
    public CashRegisterTransaction recordTransaction(Long sessionId,
                                                     CashRegisterTransactionRecordRequest request,
                                                     Long staffId) {
        CashRegisterSession session = loadSession(sessionId);
        if (!session.isOpen()) {
            throw new BusinessRuleException("Cannot record transaction on a closed session");
        }
        Transaction tx = transactionService.record(
                staffId, session, TransactionType.CASH_REGISTER,
                new Money(request.amount()), request.direction(), request.description());
        applyTransaction(tx);
        return cashTxRepository.save(CashRegisterTransaction.record(tx, request.action()));
    }

    /**
     * Moves a recorded transaction's money through its cash register session balance.
     */
    public void applyTransaction(Transaction transaction) {
        CashRegisterSession session = transaction.getCashRegisterSession();
        if (transaction.isInflow()) {
            session.deposit(transaction.getAmount());
        } else {
            session.withdraw(transaction.getAmount());
        }
    }

    // ----- discrepancies -----

    /**
     * Discrepancies from sessions operated by staff below the given manager in the
     * management tree (recursive), narrowed by the filter.
     */
    @Transactional(readOnly = true)
    public Page<CashRegisterSessionDiscrepancy> listDiscrepancies(Long managerStaffId, DiscrepancyFilterRequest filter, Pageable pageable) {
        List<Long> staffIds = staffService.findSubordinateStaffIds(managerStaffId);
        staffIds.add(managerStaffId);    // include own sessions as well
        return discrepancyRepository.findAll(
                DiscrepancySpecification.matches(filter)
                        .and(DiscrepancySpecification.staffIdIn(staffIds)),
                pageable);
    }

    /**
     * Resolve a cash register session discrepancy.
     * Once resolved, the discrepancy is marked as such and cannot be modified further.
     * The adjustment is made only by the manager of the staff member who made the discrepancy.
     */
    public CashRegisterSessionDiscrepancy resolveDiscrepancy(Long id, DiscrepancyResolveRequest request, Long staffId) {
        CashRegisterSessionDiscrepancy discrepancy = discrepancyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Discrepancy not found: " + id));
        if (discrepancy.isResolved()) {
            throw new BusinessRuleException("Discrepancy is already resolved");
        }

        Long operatorStaffId = discrepancy.getSession().getStaffId();
        if (!staffService.isManagerOf(staffId, operatorStaffId)) {
            throw new AccessDeniedException("Only the manager of the staff member who caused the discrepancy can resolve it");
        }

        discrepancy.resolve(staffId, request.resolutionNote());
        return discrepancy;
    }

    // ----- internal helpers used by other modules -----

    public CashRegisterSession requireOpenSession(Long sessionId) {
        CashRegisterSession session = loadSession(sessionId);
        if (!session.isOpen()) {
            throw new BusinessRuleException("Session " + sessionId + " is not OPEN");
        }
        return session;
    }

    private CashRegister loadRegister(Long id) {
        return cashRegisterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cash register not found: " + id));
    }

    private CashRegisterSession loadSession(Long id) {
        return sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cash register session not found: " + id));
    }
}
