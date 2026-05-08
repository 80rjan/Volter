package com.volter.shop.transaction;

import com.volter.shop.modules.cashregister.domain.model.CashRegisterTransaction;
import com.volter.shop.modules.cashregister.domain.model.enums.CashRegisterTransactionAction;
import com.volter.shop.modules.cashregister.infrastructure.mapper.CashRegisterMapperImpl;
import com.volter.shop.modules.customer.infrastructure.mapper.CustomerMapperImpl;
import com.volter.shop.modules.expense.domain.model.ExpenseTransaction;
import com.volter.shop.modules.expense.infrastructure.mapper.ExpenseMapperImpl;
import com.volter.shop.modules.inventory.infrastructure.mapper.ItemDetailedMapperImpl;
import com.volter.shop.modules.inventory.infrastructure.mapper.ItemMapperImpl;
import com.volter.shop.modules.pawn.domain.model.PawnTransaction;
import com.volter.shop.modules.pawn.domain.model.enums.PawnTransactionAction;
import com.volter.shop.modules.pawn.infrastructure.mapper.PawnMapperImpl;
import com.volter.shop.modules.sale.domain.model.SaleTransaction;
import com.volter.shop.modules.sale.domain.model.enums.SaleTransactionAction;
import com.volter.shop.modules.sale.infrastructure.mapper.SaleMapperImpl;
import com.volter.shop.modules.transaction.web.response.TransactionDetailedResponse;
import com.volter.shop.modules.transaction.web.response.TransactionResponse;
import com.volter.shop.modules.transaction.web.response.categories.TransactionDetailedCashRegisterResponse;
import com.volter.shop.modules.transaction.web.response.categories.TransactionDetailedExpenseResponse;
import com.volter.shop.modules.transaction.web.response.categories.TransactionDetailedPawnResponse;
import com.volter.shop.modules.transaction.web.response.categories.TransactionDetailedSaleResponse;
import com.volter.shop.modules.transaction.domain.model.Transaction;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionCategory;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionDirection;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionMarginType;
import com.volter.shop.modules.transaction.infrastructure.mapper.TransactionMapper;
import com.volter.shop.modules.transaction.infrastructure.mapper.TransactionMapperImpl;
import com.volter.shop.shared.valueobject.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = {
        TransactionMapperImpl.class,
        PawnMapperImpl.class,
        SaleMapperImpl.class,
        ExpenseMapperImpl.class,
        CashRegisterMapperImpl.class,
        ItemMapperImpl.class,
        ItemDetailedMapperImpl.class,
        CustomerMapperImpl.class
})
class TransactionMapperTest {

    @Autowired
    private TransactionMapper transactionMapper;

    // ─── fixtures ────────────────────────────────────────────────────────────

    private PawnTransaction pawnTransaction() {
        return PawnTransaction.builder()
                .amount(new Money(1000))
                .direction(TransactionDirection.IN)
                .marginAmount(new Money(0))
                .marginType(TransactionMarginType.NEUTRAL)
                .action(PawnTransactionAction.CREATION)
                .build();
    }

    private SaleTransaction saleTransaction() {
        return SaleTransaction.builder()
                .amount(new Money(500))
                .direction(TransactionDirection.IN)
                .marginAmount(new Money(50))
                .marginType(TransactionMarginType.PROFIT)
                .action(SaleTransactionAction.SALE)
                .build();
    }

    private ExpenseTransaction expenseTransaction() {
        return ExpenseTransaction.builder()
                .amount(new Money(300))
                .direction(TransactionDirection.OUT)
                .marginAmount(new Money(0))
                .marginType(TransactionMarginType.NEUTRAL)
                .build();
    }

    private CashRegisterTransaction cashRegisterTransaction() {
        return CashRegisterTransaction.builder()
                .amount(new Money(0))
                .direction(TransactionDirection.NEUTRAL)
                .marginAmount(new Money(0))
                .marginType(TransactionMarginType.NEUTRAL)
                .action(CashRegisterTransactionAction.OPEN_SESSION)
                .build();
    }

    // =========================================================================
    // toDetailedResponse() — subclass dispatch
    // =========================================================================

    @Nested
    @DisplayName("toDetailedResponse() — subclass dispatch")
    class ToDetailedResponseDispatch {

        @Test
        @DisplayName("PawnTransaction → TransactionDetailedPawnResponse")
        void pawnTransaction_dispatchesToPawnResponse() {
            TransactionDetailedResponse r = transactionMapper.toDetailedResponse(pawnTransaction());
            assertThat(r).isInstanceOf(TransactionDetailedPawnResponse.class);
        }

        @Test
        @DisplayName("SaleTransaction → TransactionDetailedSaleResponse")
        void saleTransaction_dispatchesToSaleResponse() {
            assertThat(transactionMapper.toDetailedResponse(saleTransaction()))
                    .isInstanceOf(TransactionDetailedSaleResponse.class);
        }

        @Test
        @DisplayName("ExpenseTransaction → TransactionDetailedExpenseResponse")
        void expenseTransaction_dispatchesToExpenseResponse() {
            assertThat(transactionMapper.toDetailedResponse(expenseTransaction()))
                    .isInstanceOf(TransactionDetailedExpenseResponse.class);
        }

        @Test
        @DisplayName("CashRegisterTransaction → TransactionDetailedCashRegisterResponse")
        void cashRegisterTransaction_dispatchesToCashRegisterResponse() {
            assertThat(transactionMapper.toDetailedResponse(cashRegisterTransaction()))
                    .isInstanceOf(TransactionDetailedCashRegisterResponse.class);
        }

        @Test
        @DisplayName("unknown Transaction subclass throws RuntimeException (SubclassExhaustiveStrategy)")
        void unknownSubclass_throwsRuntimeException() {
            Transaction unknown = new Transaction() {
                @Override
                public TransactionCategory getCategory() { return null; }
            };
            unknown.setAmount(new Money(0));
            unknown.setDirection(TransactionDirection.NEUTRAL);
            unknown.setMarginAmount(new Money(0));
            unknown.setMarginType(TransactionMarginType.NEUTRAL);

            assertThatThrownBy(() -> transactionMapper.toDetailedResponse(unknown))
                    .isInstanceOf(RuntimeException.class);
        }
    }

    // =========================================================================
    // toDetailedResponse() — category field per subtype
    // =========================================================================

    @Nested
    @DisplayName("toDetailedResponse() — category returned per type")
    class CategoryPerSubtype {

        @Test
        @DisplayName("pawn detail response returns PAWN category")
        void pawnDetailResponse_returnsPawnCategory() {
            TransactionDetailedPawnResponse r =
                    (TransactionDetailedPawnResponse) transactionMapper.toDetailedResponse(pawnTransaction());
            assertThat(r.getTransactionCategory()).isEqualTo(TransactionCategory.PAWN);
        }

        @Test
        @DisplayName("sale detail response returns SALE category")
        void saleDetailResponse_returnsSaleCategory() {
            TransactionDetailedSaleResponse r =
                    (TransactionDetailedSaleResponse) transactionMapper.toDetailedResponse(saleTransaction());
            assertThat(r.getTransactionCategory()).isEqualTo(TransactionCategory.SALE);
        }

        @Test
        @DisplayName("expense detail response returns EXPENSE category")
        void expenseDetailResponse_returnsExpenseCategory() {
            TransactionDetailedExpenseResponse r =
                    (TransactionDetailedExpenseResponse) transactionMapper.toDetailedResponse(expenseTransaction());
            assertThat(r.getTransactionCategory()).isEqualTo(TransactionCategory.EXPENSE);
        }

        @Test
        @DisplayName("cash register detail response returns CASH_REGISTER category")
        void cashRegisterDetailResponse_returnsCashRegisterCategory() {
            TransactionDetailedCashRegisterResponse r =
                    (TransactionDetailedCashRegisterResponse) transactionMapper.toDetailedResponse(cashRegisterTransaction());
            assertThat(r.getTransactionCategory()).isEqualTo(TransactionCategory.CASH_REGISTER);
        }
    }

    // =========================================================================
    // toResponse() — common field mapping
    // =========================================================================

    @Nested
    @DisplayName("toResponse() — common field mapping")
    class ToResponseFields {

        @Test
        @DisplayName("maps amount from Money to Integer")
        void mapsAmount_fromMoneyToInteger() {
            TransactionResponse r = transactionMapper.toResponse(pawnTransaction());
            assertThat(r.getAmount()).isEqualTo(1000);
        }

        @Test
        @DisplayName("maps direction")
        void mapsDirection() {
            assertThat(transactionMapper.toResponse(pawnTransaction()).getDirection())
                    .isEqualTo(TransactionDirection.IN);
        }

        @Test
        @DisplayName("maps marginAmount from Money to Integer")
        void mapsMarginAmount_fromMoneyToInteger() {
            assertThat(transactionMapper.toResponse(saleTransaction()).getMarginAmount()).isEqualTo(50);
        }

        @Test
        @DisplayName("maps marginType")
        void mapsMarginType() {
            assertThat(transactionMapper.toResponse(saleTransaction()).getMarginType())
                    .isEqualTo(TransactionMarginType.PROFIT);
        }

        @Test
        @DisplayName("null Money amount maps to null Integer")
        void nullMoneyAmount_mapsToNull() {
            PawnTransaction t = PawnTransaction.builder()
                    .amount(null)
                    .direction(TransactionDirection.NEUTRAL)
                    .marginAmount(new Money(0))
                    .marginType(TransactionMarginType.NEUTRAL)
                    .action(PawnTransactionAction.CREATION)
                    .build();
            assertThat(transactionMapper.toResponse(t).getAmount()).isNull();
        }
    }

    // =========================================================================
    // toResponse(List) — list delegation
    // =========================================================================

    @Nested
    @DisplayName("toResponse(List)")
    class ToResponseList {

        @Test
        @DisplayName("maps list of mixed transaction types — correct size")
        void mapsListCorrectly() {
            List<TransactionResponse> results = transactionMapper.toResponse(
                    List.of(pawnTransaction(), saleTransaction(), expenseTransaction(), cashRegisterTransaction()));
            assertThat(results).hasSize(4);
        }

        @Test
        @DisplayName("empty list returns empty list")
        void emptyList_returnsEmptyList() {
            assertThat(transactionMapper.toResponse(List.of())).isEmpty();
        }
    }

    // =========================================================================
    // Specific typed methods — toDetailedPawnResponse / toDetailedSaleResponse etc.
    // =========================================================================

    @Nested
    @DisplayName("typed detail methods")
    class TypedDetailMethods {

        @Test
        @DisplayName("toDetailedPawnResponse maps amount")
        void toDetailedPawnResponse_mapsAmount() {
            assertThat(transactionMapper.toDetailedPawnResponse(pawnTransaction()).getAmount())
                    .isEqualTo(1000);
        }

        @Test
        @DisplayName("toDetailedSaleResponse maps amount")
        void toDetailedSaleResponse_mapsAmount() {
            assertThat(transactionMapper.toDetailedSaleResponse(saleTransaction()).getAmount())
                    .isEqualTo(500);
        }

        @Test
        @DisplayName("toDetailedExpenseResponse maps amount")
        void toDetailedExpenseResponse_mapsAmount() {
            assertThat(transactionMapper.toDetailedExpenseResponse(expenseTransaction()).getAmount())
                    .isEqualTo(300);
        }

        @Test
        @DisplayName("toDetailedCashRegisterResponse maps amount")
        void toDetailedCashRegisterResponse_mapsAmount() {
            assertThat(transactionMapper.toDetailedCashRegisterResponse(cashRegisterTransaction()).getAmount())
                    .isZero();
        }
    }
}
