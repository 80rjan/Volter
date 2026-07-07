package skit_project.tests.logic;

import com.volter.identity.modules.staff.application.StaffService;
import com.volter.shop.modules.cashregister.application.CashRegisterService;
import com.volter.shop.modules.customer.application.CustomerService;
import com.volter.shop.modules.inventory.application.ItemService;
import com.volter.shop.modules.inventory.domain.model.Item;
import com.volter.shop.modules.inventory.domain.model.enums.ItemType;
import com.volter.shop.modules.notification.application.NotificationService;
import com.volter.shop.modules.pawn.application.PawnService;
import com.volter.shop.modules.pawn.application.dto.PawnFilterRequest;
import com.volter.shop.modules.pawn.application.dto.PawnSummaryResponse;
import com.volter.shop.modules.pawn.domain.model.PawnContract;
import com.volter.shop.modules.pawn.domain.repository.PawnContractExtensionRepository;
import com.volter.shop.modules.pawn.domain.repository.PawnContractRepository;
import com.volter.shop.modules.pawn.domain.repository.PawnTransactionRepository;
import com.volter.shop.modules.sale.application.SaleService;
import com.volter.shop.modules.transaction.application.TransactionService;
import com.volter.shop.shared.valueobject.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Logic coverage (GACC/CACC/RACC) — summarize() gold filter")
class GoldFilterLogicTest {

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

    /** Runs summarize() over the given contracts (no filter, so no staff scoping). */
    private PawnSummaryResponse summarizeOver(List<PawnContract> contracts) {
        when(contractRepository.findAll(any(Specification.class))).thenReturn(contracts);
        when(pawnTxRepository.provisionBetween(any(LocalDate.class), any(LocalDate.class))).thenReturn(0L);
        return pawnService.summarize(PawnFilterRequest.builder().build(), 1L);
    }

    private PawnContract contract(Item item, int principal, int interest) {
        PawnContract c = mock(PawnContract.class);
        when(c.getItem()).thenReturn(item);
        when(c.getPrincipalAmount()).thenReturn(new Money(principal));
        when(c.getInterestAmount()).thenReturn(new Money(interest));
        return c;
    }

    private Item goldItem(double weightGrams) {
        Item item = mock(Item.class);
        when(item.getType()).thenReturn(ItemType.GOLD);
        Map<String, Object> attrs = new HashMap<>();
        attrs.put("weightGrams", weightGrams);
        when(item.getAttributes()).thenReturn(attrs);
        return item;
    }

    private Item nonGoldItem() {
        Item item = mock(Item.class);
        when(item.getType()).thenReturn(ItemType.WATCH);
        return item;
    }

    // ----- RACC rows ----------------------------------------------------------

    @Test
    @DisplayName("(a=T, b=T) predicate TRUE -> gold weight is counted")
    void goldItem_predicateTrue_isCounted() {
        PawnSummaryResponse s = summarizeOver(List.of(contract(goldItem(5.0), 1000, 100)));

        assertEquals(5.0, s.totalGoldGrams(), 0.0001);   // predicate true -> item's grams added
        assertEquals(1L, s.count());
    }

    @Test
    @DisplayName("(a=T, b=F) predicate FALSE -> non-gold item is NOT counted")
    void nonGoldItem_predicateFalse_notCounted() {
        PawnSummaryResponse s = summarizeOver(List.of(contract(nonGoldItem(), 800, 50)));

        assertEquals(0.0, s.totalGoldGrams(), 0.0001);   // b false -> excluded
        assertEquals(1L, s.count());
    }

    @Test
    @DisplayName("(a=F, b masked) predicate FALSE -> null-item contract is NOT counted (short-circuit)")
    void nullItem_predicateFalse_notCounted() {
        PawnSummaryResponse s = summarizeOver(List.of(contract(null, 500, 20)));

        assertEquals(0.0, s.totalGoldGrams(), 0.0001);   // a false -> b never evaluated, excluded
        assertEquals(1L, s.count());
    }

    // ----- All three rows together (shows only the gold one contributes) ------

    @Test
    @DisplayName("Mixed set: only the (T,T) contract contributes to the gold grams")
    void mixedSet_onlyGoldCounted() {
        PawnSummaryResponse s = summarizeOver(List.of(
                contract(goldItem(5.0), 1000, 100),   // (T,T) counted
                contract(nonGoldItem(), 800, 50),     // (T,F) not counted
                contract(null, 500, 20)));            // (F,-) not counted

        assertEquals(5.0, s.totalGoldGrams(), 0.0001);
        assertEquals(3L, s.count());
        assertEquals(2300L, s.totalPrincipal());       // 1000 + 800 + 500
        assertEquals(170L, s.totalInterest());         // 100 + 50 + 20
    }
}
