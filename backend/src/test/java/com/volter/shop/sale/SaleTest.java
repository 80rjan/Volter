package com.volter.shop.sale;

import com.volter.shop.modules.cashregister.domain.model.CashRegisterSession;
import com.volter.shop.modules.customer.domain.model.Customer;
import com.volter.shop.modules.inventory.domain.model.enums.ItemOriginType;
import com.volter.shop.modules.inventory.domain.model.enums.ItemStatus;
import com.volter.shop.modules.inventory.domain.model.enums.types.GoldItemCarats;
import com.volter.shop.modules.inventory.domain.model.types.GoldItem;
import com.volter.shop.modules.sale.application.dto.dto.SaleFromForfeitedPawnDTO;
import com.volter.shop.modules.sale.application.dto.request.SaleCreationRequest;
import com.volter.shop.modules.sale.application.dto.result.SaleSellResult;
import com.volter.shop.modules.sale.domain.model.Sale;
import com.volter.shop.modules.sale.domain.model.SaleTransaction;
import com.volter.shop.modules.sale.domain.model.enums.SaleStatus;
import com.volter.shop.modules.sale.domain.model.enums.SaleTransactionAction;
import com.volter.shop.modules.staff.domain.model.Staff;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionDirection;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionMarginType;
import com.volter.shop.shared.valueobject.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;

class SaleTest {

    private Customer customer;
    private GoldItem item;
    private CashRegisterSession session;
    private Staff staff;

    @BeforeEach
    void setUp() {
        customer = Customer.builder()
                .name("Test IdentityUser")
                .phoneNumber("070123456")
                .embg("1234567890123")
                .address("Test St 1")
                .city("Skopje")
                .build();

        item = GoldItem.builder()
                .description("Gold ring")
                .itemOriginType(ItemOriginType.PAWN)
                .itemStatus(ItemStatus.IN_PAWN)
                .weightGrams(new BigDecimal("5.00"))
                .pricePerGram(3000)
                .carats(GoldItemCarats.CARAT_14)
                .pieceType("Ring")
                .build();

        session = mock(CashRegisterSession.class);
        staff   = mock(Staff.class);
    }

    // ─── helpers ─────────────────────────────────────────────────────────────

    private SaleCreationRequest creationRequest(int purchasePrice) {
        SaleCreationRequest req = new SaleCreationRequest();
        req.setPurchasePrice(purchasePrice);
        req.setTransactionDescription("Initial listing");
        return req;
    }

    private Sale listedSale(int purchasePrice) {
        return Sale.create(creationRequest(purchasePrice), session, staff, item, customer);
    }

    private SaleFromForfeitedPawnDTO forfeitDTO(int purchasePrice) {
        return new SaleFromForfeitedPawnDTO(SaleTransactionAction.CREATION, purchasePrice);
    }

    // =========================================================================
    // create()
    // =========================================================================

    @Nested
    class Create {

        @Test
        void sets_purchasePrice() {
            assertThat(listedSale(5000).getPurchasePrice()).isEqualTo(new Money(5000));
        }

        @Test
        void sets_customer() {
            assertThat(listedSale(5000).getCustomer()).isEqualTo(customer);
        }

        @Test
        void sets_item() {
            assertThat(listedSale(5000).getItem()).isEqualTo(item);
        }

        @Test
        void defaultStatus_isListed() {
            assertThat(listedSale(5000).getStatus()).isEqualTo(SaleStatus.LISTED);
        }

        @Test
        void defaultActive_isTrue() {
            assertThat(listedSale(5000).isActive()).isTrue();
        }

        @Test
        void soldPrice_isNull() {
            assertThat(listedSale(5000).getSoldPrice()).isNull();
        }

        @Test
        void addsOneAcquisitionTransaction() {
            Sale sale = listedSale(5000);
            assertThat(sale.getTransactions()).hasSize(1);
            assertThat(sale.getTransactions().get(0).getAction())
                    .isEqualTo(SaleTransactionAction.ACQUISITION);
        }

        @Test
        void acquisitionTransaction_directionIsOut() {
            assertThat(listedSale(5000).getTransactions().get(0).getDirection())
                    .isEqualTo(TransactionDirection.OUT);
        }

        @Test
        void acquisitionTransaction_marginIsNeutral() {
            SaleTransaction t = listedSale(5000).getTransactions().get(0);
            assertThat(t.getMarginType()).isEqualTo(TransactionMarginType.NEUTRAL);
            assertThat(t.getMarginAmount()).isEqualTo(new Money(0));
        }

        @Test
        void acquisitionTransaction_amountMatchesPurchasePrice() {
            assertThat(listedSale(3000).getTransactions().get(0).getAmount())
                    .isEqualTo(new Money(3000));
        }
    }

    // =========================================================================
    // moveFromPawn()
    // =========================================================================

    @Nested
    class MoveFromPawn {

        @Test
        void sets_purchasePrice() {
            Sale sale = Sale.moveFromPawn(forfeitDTO(8000), session, staff, item, customer);
            assertThat(sale.getPurchasePrice()).isEqualTo(new Money(8000));
        }

        @Test
        void sets_customer() {
            Sale sale = Sale.moveFromPawn(forfeitDTO(8000), session, staff, item, customer);
            assertThat(sale.getCustomer()).isEqualTo(customer);
        }

        @Test
        void sets_item() {
            Sale sale = Sale.moveFromPawn(forfeitDTO(8000), session, staff, item, customer);
            assertThat(sale.getItem()).isEqualTo(item);
        }

        @Test
        void defaultStatus_isListed() {
            Sale sale = Sale.moveFromPawn(forfeitDTO(8000), session, staff, item, customer);
            assertThat(sale.getStatus()).isEqualTo(SaleStatus.LISTED);
        }

        @Test
        void addsOneCreationTransaction() {
            Sale sale = Sale.moveFromPawn(forfeitDTO(8000), session, staff, item, customer);
            assertThat(sale.getTransactions()).hasSize(1);
            assertThat(sale.getTransactions().get(0).getAction())
                    .isEqualTo(SaleTransactionAction.CREATION);
        }

        @Test
        void creationTransaction_directionIsNeutral() {
            Sale sale = Sale.moveFromPawn(forfeitDTO(8000), session, staff, item, customer);
            assertThat(sale.getTransactions().get(0).getDirection())
                    .isEqualTo(TransactionDirection.NEUTRAL);
        }

        @Test
        void creationTransaction_amountIsZero() {
            Sale sale = Sale.moveFromPawn(forfeitDTO(8000), session, staff, item, customer);
            assertThat(sale.getTransactions().get(0).getAmount()).isEqualTo(new Money(0));
        }

        @Test
        void creationTransaction_marginIsNeutral() {
            Sale sale = Sale.moveFromPawn(forfeitDTO(8000), session, staff, item, customer);
            SaleTransaction t = sale.getTransactions().get(0);
            assertThat(t.getMarginType()).isEqualTo(TransactionMarginType.NEUTRAL);
            assertThat(t.getMarginAmount()).isEqualTo(new Money(0));
        }

        @Test
        void creationTransaction_descriptionIsNull() {
            Sale sale = Sale.moveFromPawn(forfeitDTO(8000), session, staff, item, customer);
            assertThat(sale.getTransactions().get(0).getDescription()).isNull();
        }
    }

    // =========================================================================
    // sell()
    // =========================================================================

    @Nested
    class Sell {

        @Test
        void sets_soldPrice() {
            Sale sale = listedSale(5000);
            sale.sell(new Money(6000), "sold", session, staff);
            assertThat(sale.getSoldPrice()).isEqualTo(new Money(6000));
        }

        @Test
        void sets_statusToSold() {
            Sale sale = listedSale(5000);
            sale.sell(new Money(6000), "sold", session, staff);
            assertThat(sale.getStatus()).isEqualTo(SaleStatus.SOLD);
        }

        @Test
        void profit_whenSoldPriceAbovePurchasePrice() {
            Sale sale = listedSale(5000);
            SaleSellResult result = sale.sell(new Money(6000), "sold", session, staff);
            assertThat(result.underpaid()).isFalse();
            assertThat(result.transaction().getMarginType()).isEqualTo(TransactionMarginType.PROFIT);
        }

        @Test
        void loss_whenSoldPriceBelowPurchasePrice() {
            Sale sale = listedSale(5000);
            SaleSellResult result = sale.sell(new Money(4000), "sold", session, staff);
            assertThat(result.underpaid()).isTrue();
            assertThat(result.transaction().getMarginType()).isEqualTo(TransactionMarginType.LOSS);
        }

        @Test
        void profit_whenSoldPriceEqualsPurchasePrice() {
            Sale sale = listedSale(5000);
            SaleSellResult result = sale.sell(new Money(5000), "even", session, staff);
            assertThat(result.underpaid()).isFalse();
            assertThat(result.transaction().getMarginType()).isEqualTo(TransactionMarginType.PROFIT);
        }

        @Test
        void marginAmount_isAbsoluteDifference_profit() {
            Sale sale = listedSale(5000);
            SaleSellResult result = sale.sell(new Money(6500), "sold", session, staff);
            assertThat(result.transaction().getMarginAmount()).isEqualTo(new Money(1500));
        }

        @Test
        void marginAmount_isAbsoluteDifference_loss() {
            Sale sale = listedSale(5000);
            SaleSellResult result = sale.sell(new Money(3000), "sold", session, staff);
            assertThat(result.transaction().getMarginAmount()).isEqualTo(new Money(2000));
        }

        @Test
        void saleTransaction_directionIsIn() {
            Sale sale = listedSale(5000);
            SaleSellResult result = sale.sell(new Money(6000), "sold", session, staff);
            assertThat(result.transaction().getDirection()).isEqualTo(TransactionDirection.IN);
        }

        @Test
        void saleTransaction_actionIsSale() {
            Sale sale = listedSale(5000);
            SaleSellResult result = sale.sell(new Money(6000), "sold", session, staff);
            assertThat(result.transaction().getAction()).isEqualTo(SaleTransactionAction.SALE);
        }

        @Test
        void addsTransactionToList() {
            Sale sale = listedSale(5000);
            sale.sell(new Money(6000), "sold", session, staff);
            // 1 acquisition + 1 sale
            assertThat(sale.getTransactions()).hasSize(2);
        }

        @Test
        void throwsWhenAlreadySold() {
            Sale sale = listedSale(5000);
            sale.sell(new Money(6000), "first", session, staff);
            assertThatThrownBy(() -> sale.sell(new Money(7000), "second", session, staff))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Only listed sales can be sold");
        }

        @Test
        void throwsWhenStatusIsNotListed() {
            Sale sale = Sale.builder()
                    .purchasePrice(new Money(5000))
                    .status(SaleStatus.SOLD)
                    .customer(customer)
                    .item(item)
                    .build();
            assertThatThrownBy(() -> sale.sell(new Money(5000), "desc", session, staff))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        void sellFromForfeitedPawn_works() {
            // verifies sell() also works on a sale created via moveFromPawn
            Sale sale = Sale.moveFromPawn(forfeitDTO(8000), session, staff, item, customer);
            assertThatNoException().isThrownBy(
                    () -> sale.sell(new Money(9000), "sold from forfeit", session, staff)
            );
            assertThat(sale.getStatus()).isEqualTo(SaleStatus.SOLD);
        }
    }

    // =========================================================================
    // getInitialTransaction()
    // =========================================================================

    @Nested
    class GetInitialTransaction {

        @Test
        void returnsAcquisitionTransaction_fromCreate() {
            Sale sale = listedSale(5000);
            assertThat(sale.getInitialTransaction().getAction())
                    .isEqualTo(SaleTransactionAction.ACQUISITION);
        }

        @Test
        void returnsCreationTransaction_fromMoveFromPawn() {
            Sale sale = Sale.moveFromPawn(forfeitDTO(8000), session, staff, item, customer);
            assertThat(sale.getInitialTransaction().getAction())
                    .isEqualTo(SaleTransactionAction.CREATION);
        }

        @Test
        void stillReturnsInitialTransaction_afterSell() {
            Sale sale = listedSale(5000);
            sale.sell(new Money(6000), "sold", session, staff);
            assertThat(sale.getInitialTransaction().getAction())
                    .isEqualTo(SaleTransactionAction.ACQUISITION);
        }

        @Test
        void throwsWhenNoCreationOrAcquisitionTransaction() {
            Sale sale = Sale.builder()
                    .purchasePrice(new Money(5000))
                    .customer(customer)
                    .item(item)
                    .build();
            assertThatThrownBy(sale::getInitialTransaction)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Initial creation transaction not found");
        }
    }
}
