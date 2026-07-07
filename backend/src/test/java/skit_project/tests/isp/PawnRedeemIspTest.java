package skit_project.tests.isp;

import com.volter.identity.modules.staff.application.StaffService;
import com.volter.shop.modules.cashregister.application.CashRegisterService;
import com.volter.shop.modules.cashregister.domain.model.CashRegisterSession;
import com.volter.shop.modules.customer.application.CustomerService;
import com.volter.shop.modules.inventory.application.ItemService;
import com.volter.shop.modules.inventory.domain.model.Item;
import com.volter.shop.modules.notification.application.NotificationService;
import com.volter.shop.modules.notification.domain.model.enums.NotificationType;
import com.volter.shop.modules.pawn.application.PawnService;
import com.volter.shop.modules.pawn.application.dto.PawnRedeemRequest;
import com.volter.shop.modules.pawn.domain.model.PawnContract;
import com.volter.shop.modules.pawn.domain.model.PawnTransaction;
import com.volter.shop.modules.pawn.domain.repository.PawnContractExtensionRepository;
import com.volter.shop.modules.pawn.domain.repository.PawnContractRepository;
import com.volter.shop.modules.pawn.domain.repository.PawnTransactionRepository;
import com.volter.shop.modules.sale.application.SaleService;
import com.volter.shop.modules.transaction.application.TransactionService;
import com.volter.shop.modules.transaction.domain.model.Transaction;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionDirection;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionType;
import com.volter.shop.shared.valueobject.Money;
import com.volter.shared.web.exception.BusinessRuleException;
import com.volter.shared.web.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ISP / BCC — PawnService.redeem()")
class PawnRedeemIspTest {

    @Mock private PawnContractRepository contractRepository;
    @Mock private PawnContractExtensionRepository extensionRepository;
    @Mock private PawnTransactionRepository pawnTxRepository;
    @Mock private CustomerService customerService;
    @Mock private ItemService itemService;
    @Mock private CashRegisterService cashRegisterService;
    @Mock private TransactionService transactionService;
    @Mock private SaleService saleService;
    @Mock private StaffService staffService;
    @Mock private NotificationService notificationService;

    @InjectMocks private PawnService pawnService;

    private static final long CONTRACT_ID = 1L;
    private static final long SESSION_ID = 77L;
    private static final long STAFF_ID = 3L;

    /** Stub the collaborators reached on the happy path up to the underpayment check. */
    private PawnContract stubReachableContract(int expectedAmount, int paidAmount) {
        PawnContract contract = mock(PawnContract.class);
        Item item = mock(Item.class);
        CashRegisterSession session = mock(CashRegisterSession.class);
        Transaction tx = mock(Transaction.class);
        when(contractRepository.findById(CONTRACT_ID)).thenReturn(Optional.of(contract));
        when(cashRegisterService.requireOpenSession(SESSION_ID)).thenReturn(session);
        when(contract.getItem()).thenReturn(item);
        when(contract.expectedRedemptionAmount()).thenReturn(new Money(expectedAmount));
        when(transactionService.record(eq(STAFF_ID), eq(session), eq(TransactionType.PAWN),
                eq(new Money(paidAmount)), eq(TransactionDirection.IN), anyString())).thenReturn(tx);
        return contract;
    }

    // ----- Base test -----------------------------------------------------------

    @Test
    @DisplayName("BASE (T,T,equal,T): redeems, moves cash IN, raises no risk flag")
    void base_paidEqualsExpected_noFlag() {
        // paid (1120) == expected (1120)  -> not underpaid
        PawnContract contract = stubReachableContract(1120, 1120);

        PawnContract result = pawnService.redeem(CONTRACT_ID, new PawnRedeemRequest(1120, SESSION_ID), STAFF_ID);

        assertSame(contract, result);
        verify(contract).redeem();
        verify(itemService).markRedeemed(contract.getItem(), STAFF_ID);
        verify(cashRegisterService).applyTransaction(any(Transaction.class));
        verify(pawnTxRepository).save(any(PawnTransaction.class));
        verifyNoInteractions(notificationService);           // no underpayment -> no manager notified
    }

    // ----- One test per non-base block ----------------------------------------

    @Test
    @DisplayName("IC1=F (missing contract): throws ResourceNotFoundException")
    void ic1_missingContract_throws() {
        when(contractRepository.findById(CONTRACT_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> pawnService.redeem(CONTRACT_ID, new PawnRedeemRequest(1120, SESSION_ID), STAFF_ID));
        verifyNoInteractions(transactionService, notificationService);
    }

    @Test
    @DisplayName("IC2=F (session not open): throws before any money moves")
    void ic2_sessionNotOpen_throws() {
        when(contractRepository.findById(CONTRACT_ID)).thenReturn(Optional.of(mock(PawnContract.class)));
        when(cashRegisterService.requireOpenSession(SESSION_ID))
                .thenThrow(new BusinessRuleException("No open cash register session"));

        assertThrows(BusinessRuleException.class,
                () -> pawnService.redeem(CONTRACT_ID, new PawnRedeemRequest(1120, SESSION_ID), STAFF_ID));
        verifyNoInteractions(transactionService, notificationService);
    }

    @Test
    @DisplayName("FC1=less (underpaid) with FC2=T: notifies the manager with a RISK_FLAG")
    void fc1_underpaid_withManager_raisesRiskFlag() {
        PawnContract contract = stubReachableContract(1120, 1000);   // paid 1000 < expected 1120
        PawnTransaction pawnTx = mock(PawnTransaction.class);
        when(contract.getId()).thenReturn(CONTRACT_ID);
        when(pawnTxRepository.save(any(PawnTransaction.class))).thenReturn(pawnTx);
        when(pawnTx.getId()).thenReturn(555L);
        when(staffService.findManagerId(STAFF_ID)).thenReturn(Optional.of(9L));

        pawnService.redeem(CONTRACT_ID, new PawnRedeemRequest(1000, SESSION_ID), STAFF_ID);

        verify(notificationService).create(eq(9L), eq(NotificationType.RISK_FLAG),
                anyString(), anyString(), eq("pawn_transaction"), eq(555L));
    }

    @Test
    @DisplayName("FC1=greater (overpaid): no risk flag")
    void fc1_overpaid_noFlag() {
        stubReachableContract(1120, 1200);   // paid 1200 > expected 1120

        pawnService.redeem(CONTRACT_ID, new PawnRedeemRequest(1200, SESSION_ID), STAFF_ID);

        verifyNoInteractions(notificationService);
    }

    @Test
    @DisplayName("FC2=F (no manager): underpayment is not delivered to anyone")
    void fc2_underpaid_noManager_noNotification() {
        // FC2 is only observable on an underpayment, so this row uses FC1=less (see isp-explanation.txt).
        // No manager is found, so the notification lambda (which would read contract.getId()) never runs.
        stubReachableContract(1120, 1000);
        when(pawnTxRepository.save(any(PawnTransaction.class))).thenReturn(mock(PawnTransaction.class));
        when(staffService.findManagerId(STAFF_ID)).thenReturn(Optional.empty());

        pawnService.redeem(CONTRACT_ID, new PawnRedeemRequest(1000, SESSION_ID), STAFF_ID);

        verifyNoInteractions(notificationService);
    }
}
