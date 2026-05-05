package com.volter.shop.modules.cashregister.application;

import com.volter.shop.modules.cashregister.application.dto.request.CashRegisterSessionCloseRequest;
import com.volter.shop.modules.cashregister.application.dto.request.CashRegisterSessionDepositRequest;
import com.volter.shop.modules.cashregister.application.dto.request.CashRegisterSessionOpenRequest;
import com.volter.shop.modules.cashregister.application.dto.request.CashRegisterSessionWithdrawRequest;
import com.volter.shop.modules.cashregister.domain.model.CashRegister;
import com.volter.shop.modules.cashregister.domain.model.CashRegisterSession;
import com.volter.shop.modules.cashregister.domain.repository.CashRegisterRepository;
import com.volter.shop.modules.cashregister.domain.repository.CashRegisterSessionRepository;
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

@Service
@RequiredArgsConstructor
public class CashRegisterService {

    private final CashRegisterRepository cashRegisterRepository;
    private final CashRegisterSessionRepository cashRegisterSessionRepository;
    private final StaffService staffService;

    @Lazy
    @Autowired
    private PawnService pawnService;

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
        session.close(new Money(request.closingBalance()), staff);

        return cashRegisterSessionRepository.save(session);
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
