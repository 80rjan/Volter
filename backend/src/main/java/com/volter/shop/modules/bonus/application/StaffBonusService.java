package com.volter.shop.modules.bonus.application;

import com.volter.identity.modules.staff.application.StaffService;
import com.volter.identity.modules.staff.domain.model.Staff;
import com.volter.platform.modules.notification.application.NotificationService;
import com.volter.platform.modules.notification.domain.model.enums.NotificationType;
import com.volter.platform.modules.shop.domain.model.StaffShop;
import com.volter.platform.modules.shop.domain.repository.StaffShopRepository;
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
import com.volter.shared.web.exception.BusinessRuleException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;

/**
 * Profit-share bonus for staff. A staff member's bonus accrues continuously as
 * they generate profit (pawn provision + sale margin) and is withdrawn as cash
 * from an open session, tracked as {@link TransactionType#STAFF_BONUS}
 * transactions — so available is always {@code earned − taken}, and partial
 * withdrawals never lose the remainder. Everything is scoped to a shop and to the
 * staff member's baseline ({@code staff_shop.bonus_since}).
 */
@Service
@RequiredArgsConstructor
@Transactional
public class StaffBonusService {

    private final StaffService staffService;
    private final StaffShopRepository staffShopRepository;
    private final PawnTxQueryService pawnTxQueryService;
    private final SaleTxQueryService saleTxQueryService;
    private final TransactionService transactionService;
    private final CashRegisterService cashRegisterService;
    private final NotificationService notificationService;

    /**
     * The bonus ledger for {@code staffId} in {@code shopId}. Visible to the staff
     * member themselves or to a manager above them ({@link StaffService#get} enforces this).
     */
    @Transactional(readOnly = true)
    public StaffBonusAvailableResponse available(Long staffId, Long shopId, Long callerStaffId) {
        Staff staff = staffService.get(staffId, callerStaffId);
        return compute(staff, baselineSince(staffId, shopId));
    }

    /**
     * Withdraw part (or all) of the caller's available bonus as cash out of an open
     * session, and notify their manager.
     */
    public void withdraw(Long callerStaffId, Long shopId, StaffBonusWithdrawRequest request) {
        Staff staff = staffService.get(callerStaffId, callerStaffId);
        StaffBonusAvailableResponse ledger = compute(staff, baselineSince(callerStaffId, shopId));

        int amount = request.amount();
        if (amount > ledger.available()) {
            throw new BusinessRuleException("Amount exceeds the available bonus");
        }

        CashRegisterSession session = cashRegisterService.requireOpenSession(request.cashRegisterSessionId());
        Transaction tx = transactionService.record(
                callerStaffId, session, TransactionType.STAFF_BONUS,
                new Money(amount), TransactionDirection.OUT, "Staff bonus withdrawal");
        cashRegisterService.applyTransaction(tx);

        notifyManager(callerStaffId, staff, amount, ledger.available());
    }

    private OffsetDateTime baselineSince(Long staffId, Long shopId) {
        return staffShopRepository.findByStaffIdAndShop_Id(staffId, shopId)
                .map(StaffShop::getBonusSince)
                .orElseThrow(() -> new BusinessRuleException("Staff is not assigned to this shop"));
    }

    private StaffBonusAvailableResponse compute(Staff staff, OffsetDateTime since) {
        Long staffId = staff.getId();
        long profitBase = pawnTxQueryService.provisionForStaffSince(staffId, since)
                + saleTxQueryService.marginForStaffSince(staffId, since);
        long earned = staff.getProfitSharePercent()
                .multiply(BigDecimal.valueOf(profitBase))
                .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP)
                .longValue();
        long taken = transactionService.staffBonusTakenSince(staffId, since);
        long available = Math.max(0, earned - taken);
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
