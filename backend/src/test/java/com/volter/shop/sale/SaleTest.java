package com.volter.shop.sale;

import com.volter.shop.modules.customer.domain.model.Customer;
import com.volter.shop.modules.inventory.domain.model.Item;
import com.volter.shop.modules.sale.domain.model.Sale;
import com.volter.shop.modules.sale.domain.model.enums.SaleStatus;
import com.volter.shop.shared.valueobject.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class SaleTest {

    private Sale listing(int purchasePrice) {
        return Sale.create(mock(Customer.class), mock(Item.class), 3L, new Money(purchasePrice));
    }

    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        @DisplayName("a new listing is AVAILABLE with no sale price or profit yet")
        void newListingIsAvailable() {
            Sale sale = listing(1000);

            assertEquals(SaleStatus.AVAILABLE, sale.getStatus());
            assertTrue(sale.isAvailable());
            assertEquals(new Money(1000), sale.getPurchasePrice());
            assertNull(sale.getSalePrice());
            assertNull(sale.profit());
        }
    }

    @Nested
    @DisplayName("sell()")
    class Sell {

        @Test
        @DisplayName("marks the listing SOLD and records the sale price and time")
        void marksSold() {
            Sale sale = listing(1000);

            sale.sell(new Money(1500));

            assertEquals(SaleStatus.SOLD, sale.getStatus());
            assertEquals(new Money(1500), sale.getSalePrice());
            assertNotNull(sale.getSoldAt());
            assertFalse(sale.isAvailable());
        }

        @Test
        @DisplayName("profit is sale price minus purchase price")
        void profit() {
            Sale sale = listing(1000);

            sale.sell(new Money(1500));

            assertEquals(500, sale.profit());
        }

        @Test
        @DisplayName("selling below cost yields a negative profit and is underwater")
        void belowCost_isUnderwater() {
            Sale sale = listing(1000);

            sale.sell(new Money(800));

            assertEquals(-200, sale.profit());
            assertTrue(sale.isUnderwater());
        }

        @Test
        @DisplayName("selling at or above cost is not underwater")
        void atOrAboveCost_notUnderwater() {
            Sale sale = listing(1000);

            sale.sell(new Money(1000));

            assertFalse(sale.isUnderwater());
        }

        @Test
        @DisplayName("an unsold listing is never underwater")
        void unsold_notUnderwater() {
            assertFalse(listing(1000).isUnderwater());
        }

        @Test
        @DisplayName("only an AVAILABLE listing can be sold")
        void doubleSell_throws() {
            Sale sale = listing(1000);
            sale.sell(new Money(1500));

            assertThrows(IllegalStateException.class, () -> sale.sell(new Money(1600)));
        }
    }

    @Nested
    @DisplayName("cancel()")
    class Cancel {

        @Test
        @DisplayName("marks the listing CANCELED")
        void marksCanceled() {
            Sale sale = listing(1000);

            sale.cancel();

            assertEquals(SaleStatus.CANCELED, sale.getStatus());
        }

        @Test
        @DisplayName("a sold listing cannot be canceled")
        void soldThenCancel_throws() {
            Sale sale = listing(1000);
            sale.sell(new Money(1500));

            assertThrows(IllegalStateException.class, sale::cancel);
        }

        @Test
        @DisplayName("a canceled listing cannot be sold")
        void canceledThenSell_throws() {
            Sale sale = listing(1000);
            sale.cancel();

            assertThrows(IllegalStateException.class, () -> sale.sell(new Money(1500)));
        }
    }
}
