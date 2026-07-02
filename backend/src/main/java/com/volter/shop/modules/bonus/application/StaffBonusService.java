package com.volter.shop.modules.bonus.application;

import com.volter.identity.modules.staff.application.StaffService;
import com.volter.identity.modules.staff.domain.model.Staff;
import com.volter.shop.modules.notification.application.NotificationService;
import com.volter.shop.modules.notification.domain.model.enums.NotificationType;
import com.volter.shop.modules.bonus.application.dto.StaffBonusAvailableResponse;
import com.volter.shop.modules.bonus.application.dto.StaffBonusWithdrawRequest;
import com.volter.shop.modules.cashregister.application.CashRegisterService;
import com.volter.shop.modules.cashregister.domain.model.CashRegisterSession;
import com.volter.shop.modules.pawn.application.PawnTxQueryService;
import com.volter.shop.modules.sale.application.SaleTxQueryService;
import com.volter.shop.modules.transaction.application.TransactionService;
import com.volter.shop.modules.transaction.domain.model.Transaction;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionDirection;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionType;
import com.volter.shop.shared.valueobject.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;

/**
 * Profit-share bonus for staff. A staff member's bonus is a percentage of the
 * net profit they contributed <em>this calendar month</em> — their own pawn
 * provision plus sale margin, minus the shop's expenses — and is withdrawn as
 * cash from an open session, tracked as {@link TransactionType#STAFF_BONUS}
 * transactions. So available is always {@code earned − taken} within the month,
 * and partial withdrawals never lose the remainder. The window resets on the 1st
 * of each month: anything not withdrawn by month end does not carry over.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class StaffBonusService {

    private final StaffService staffService;
    private final PawnTxQueryService pawnTxQueryService;
    private final SaleTxQueryService saleTxQueryService;
    private final TransactionService transactionService;
    private final CashRegisterService cashRegisterService;
    private final NotificationService notificationService;

    /**
     * The bonus ledger for {@code staffId} this month. Visible to the staff member
     * themselves or to a manager above them ({@link StaffService#get} enforces this).
     */
    @Transactional(readOnly = true)
    public StaffBonusAvailableResponse available(Long staffId, Long callerStaffId) {
        Staff staff = staffService.get(staffId, callerStaffId);
        return compute(staff, startOfCurrentMonth());
    }

    /**
     * Withdraw bonus as cash out of an open session, and notify their manager. The
     * amount is NOT capped by the computed available: staff may overdraw (take more
     * than they have earned this month), which simply drives available negative.
     */
    public void withdraw(Long callerStaffId, StaffBonusWithdrawRequest request) {
        Staff staff = staffService.get(callerStaffId, callerStaffId);
        StaffBonusAvailableResponse ledger = compute(staff, startOfCurrentMonth());

        int amount = request.amount();

        CashRegisterSession session = cashRegisterService.requireOpenSession(request.cashRegisterSessionId());
        Transaction tx = transactionService.record(
                callerStaffId, session, TransactionType.STAFF_BONUS,
                new Money(amount), TransactionDirection.OUT, "Подигнат бонус");
        cashRegisterService.applyTransaction(tx);

        notifyManager(callerStaffId, staff, amount, ledger.available());
    }

    /** Start of the current calendar month (00:00 in the system zone); the monthly reset point. */
    private static OffsetDateTime startOfCurrentMonth() {
        return LocalDate.now().withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime();
    }

    private StaffBonusAvailableResponse compute(Staff staff, OffsetDateTime since) {
        Long staffId = staff.getId();
        // Own revenue (this staff) minus the shop's expenses (all staff) => net profit base.
        long revenue = pawnTxQueryService.provisionForStaffSince(staffId, since)
                + saleTxQueryService.marginForStaffSince(staffId, since);
        long profitBase = revenue - transactionService.shopExpensesSince(since);
        long earned = staff.getProfitSharePercent()
                .multiply(BigDecimal.valueOf(profitBase))
                .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP)
                .longValue();
        long taken = transactionService.staffBonusTakenSince(staffId, since);
        long available = earned - taken;
        return new StaffBonusAvailableResponse(staffId, staff.getProfitSharePercent(), profitBase, earned, taken, available);
    }

    private void notifyManager(Long staffId, Staff staff, int amount, long availableBefore) {
        staffService.findManagerId(staffId).ifPresent(managerId ->
                notificationService.create(
                        managerId,
                        NotificationType.STAFF_BONUS,
                        "Земен бонус",
                        staff.getFullName() + " зеде " + amount + " ден. бонус (достапно беше "
                                + availableBefore + " ден.).",
                        null, null));
    }
}
