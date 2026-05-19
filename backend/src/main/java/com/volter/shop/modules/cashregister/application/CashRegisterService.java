package com.volter.shop.modules.cashregister.application;

import com.volter.shop.modules.alert.application.RiskAlertService;
import com.volter.shop.modules.alert.domain.RiskAlert;
import com.volter.shop.modules.alert.domain.enums.RiskAlertSeverity;
import com.volter.shop.modules.alert.domain.enums.RiskAlertType;
import com.volter.shop.modules.cashregister.application.dto.CashRegisterSessionCloseResult;
import com.volter.shop.modules.cashregister.domain.model.enums.CashRegisterSessionDiscrepancyType;
import com.volter.shop.modules.cashregister.web.request.CashRegisterSessionCloseRequest;
import com.volter.shop.modules.cashregister.web.request.CashRegisterSessionDepositRequest;
import com.volter.shop.modules.cashregister.web.request.CashRegisterSessionOpenRequest;
import com.volter.shop.modules.cashregister.web.request.CashRegisterSessionWithdrawRequest;
import com.volter.shop.modules.cashregister.domain.model.CashRegister;
import com.volter.shop.modules.cashregister.domain.model.CashRegisterSession;
import com.volter.shop.modules.cashregister.infrastructure.CashRegisterRepository;
import com.volter.shop.modules.cashregister.infrastructure.CashRegisterSessionRepository;
import com.volter.shop.modules.pawn.application.PawnService;
import com.volter.shop.modules.pawn.domain.model.Pawn;
import com.volter.shop.shared.common.exceptions.ResourceNotFoundException;
import com.volter.shop.modules.staff.domain.model.Staff;
import com.volter.shop.modules.staff.application.StaffService;
import com.volter.shop.shared.valueobject.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CashRegisterService {

    private final CashRegisterRepository cashRegisterRepository;
    private final CashRegisterSessionRepository cashRegisterSessionRepository;
    private final StaffService staffService;

    @Lazy
    @Autowired
    private PawnService pawnService;
    @Autowired
    private RiskAlertService riskAlertService;

    public List<CashRegister> getAll() {
        return cashRegisterRepository.findAll();
    }

    @Transactional
    public CashRegister getById(Long id) {
        return cashRegisterRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Cash register with ID " + id + " not found")
        );
    }

    @Transactional
    public CashRegisterSession getSessionById(Long id) {
        return cashRegisterSessionRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Cash register session with ID " + id + " not found")
        );
    }

    @Transactional
    public CashRegister getReferenceById(Long id) {
        return cashRegisterRepository.getReferenceById(id);
    }

    @Transactional
    public CashRegister save(CashRegister cashRegister) {
        return cashRegisterRepository.save(cashRegister);
    }

    @Transactional
    public CashRegisterSession saveSession(CashRegisterSession session) {
        return cashRegisterSessionRepository.save(session);
    }

    @Transactional
    public CashRegisterSession getOpenSessionByStaff(Long staffId) {
        CashRegisterSession session = cashRegisterSessionRepository.findOpenSessionByStaffId(staffId);
        if (session == null) {
            throw new ResourceNotFoundException("No open cash register session found for staff with ID " + staffId);
        }
        return session;
    }

    @Transactional
    public CashRegisterSession getOpenSessionByStaff() {
        Staff staff = staffService.getCurrentStaff();
        CashRegisterSession session = cashRegisterSessionRepository.findOpenSessionByStaffId(staff.getId());
        if (session == null) {
            throw new ResourceNotFoundException("No open cash register session found for staff with ID " + staff.getId());
        }
        return session;
    }

    @Transactional
    @PreAuthorize("hasAuthority(T(com.volter.identity.domain.model.enums.PermissionEnum)" +
            ".CASH_REGISTER_SESSION_OPEN.permission)")
    public CashRegisterSession openSession(CashRegisterSessionOpenRequest request) {
        Staff staff = staffService.getCurrentStaff();

        if (cashRegisterSessionRepository.existsByStaffIdAndStatusOpen(staff.getId()))
            throw new IllegalStateException("Staff with ID " + staff.getId() + " already has an open cash register session");

        CashRegister cashRegister = getById(request.cashRegisterId());
        List<Pawn> maturingPawns = pawnService.getAllMaturingWithinDays(0);

        CashRegisterSession session = CashRegisterSession.create(
                new Money(request.openingBalance()),
                cashRegister,
                staff,
                maturingPawns
        );

        return cashRegisterSessionRepository.save(session);
    }

    @Transactional
    @PreAuthorize("hasAuthority(T(com.volter.identity.domain.model.enums.PermissionEnum)" +
            ".CASH_REGISTER_SESSION_CLOSE.permission)")
    public CashRegisterSession closeSession(CashRegisterSessionCloseRequest request) {
        Staff staff = staffService.getCurrentStaff();

        CashRegisterSession session = getOpenSessionByStaff(staff.getId());
        CashRegisterSessionCloseResult closeResult = session.close(new Money(request.closingBalance()), staff);

        CashRegisterSession resultSession = cashRegisterSessionRepository.saveAndFlush(session);  // flush forces persist() on closeTransaction so its ID is set before risk alert references it
        
        if (!closeResult.discrepancyType().equals(CashRegisterSessionDiscrepancyType.NONE)) {
            RiskAlert riskAlert = RiskAlert.builder()
                    .type(RiskAlertType.CASH_REGISTER_DISCREPANCY)
                    .severity(RiskAlertSeverity.MEDIUM)
                    .metadata(Map.of(
                            "sessionId", session.getId(),
                            "staffId", staff.getId(),
                            "discrepancyAmount", closeResult.discrepancy().amount(),
                            "discrepancyType", closeResult.discrepancyType().name()
                    ))
                    .summary("Cash register session closed with discrepancy")
                    .transaction(closeResult.closeTransaction())
                    .manager(staff.getManager() == null ? staff : staff.getManager())        // if null then manager did it himself
                    .build();

            riskAlertService.save(riskAlert);
        }

        return resultSession;
    }

    @Transactional
    @PreAuthorize("hasAuthority(T(com.volter.identity.domain.model.enums.PermissionEnum)" +
            ".CASH_REGISTER_DEPOSIT.permission)")
    public CashRegisterSession deposit(CashRegisterSessionDepositRequest request) {
        Staff staff = staffService.getCurrentStaff();
        CashRegisterSession cashRegisterSession = getOpenSessionByStaff(staff.getId());

        cashRegisterSession.deposit(new Money(request.depositAmount()), request.transactionDescription());

        return cashRegisterSessionRepository.save(cashRegisterSession);
    }

    @Transactional
    @PreAuthorize("hasAuthority(T(com.volter.identity.domain.model.enums.PermissionEnum)" +
            ".CASH_REGISTER_WITHDRAW.permission)")
    public CashRegisterSession withdraw(CashRegisterSessionWithdrawRequest request) {
        Staff staff = staffService.getCurrentStaff();
        CashRegisterSession cashRegisterSession = getOpenSessionByStaff(staff.getId());

        cashRegisterSession.withdraw(new Money(request.withdrawAmount()), request.transactionDescription());

        return cashRegisterSessionRepository.save(cashRegisterSession);
    }
}
