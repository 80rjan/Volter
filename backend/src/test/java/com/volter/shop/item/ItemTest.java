package com.volter.shop.item;

import com.volter.shop.modules.inventory.domain.model.Item;
import com.volter.shop.modules.inventory.domain.model.ItemStatusHistory;
import com.volter.shop.modules.inventory.domain.model.enums.ItemOriginType;
import com.volter.shop.modules.inventory.domain.model.enums.ItemStatus;
import com.volter.shop.modules.inventory.domain.model.enums.ItemType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    private Item item(ItemStatus status) {
        return Item.builder()
                .type(ItemType.GOLD)
                .origin(ItemOriginType.PAWN)
                .status(status)
                .attributes(new HashMap<>())
                .build();
    }

    @Nested
    @DisplayName("changeStatus()")
    class ChangeStatus {

        @Test
        @DisplayName("moves the item and returns a history record of the transition")
        void returnsHistory() {
            Item item = item(ItemStatus.REDEEMED);

            ItemStatusHistory history = item.changeStatus(ItemStatus.IN_PAWN, 5L);

            assertEquals(ItemStatus.IN_PAWN, item.getStatus());
            assertEquals(ItemStatus.REDEEMED, history.getFromStatus());
            assertEquals(ItemStatus.IN_PAWN, history.getToStatus());
            assertEquals(5L, history.getChangedByStaffId());
        }

        @Test
        @DisplayName("mark helpers move the item to the matching status")
        void markHelpers() {
            assertEquals(ItemStatus.IN_PAWN, statusAfter(item(ItemStatus.REDEEMED).markInPawn(7L)));
            assertEquals(ItemStatus.REDEEMED, statusAfter(item(ItemStatus.IN_PAWN).markRedeemed(7L)));
            assertEquals(ItemStatus.IN_SALE, statusAfter(item(ItemStatus.REDEEMED).markInSale(7L)));
            assertEquals(ItemStatus.SOLD, statusAfter(item(ItemStatus.IN_SALE).markSold(7L)));
        }

        private ItemStatus statusAfter(ItemStatusHistory history) {
            return history.getToStatus();
        }
    }

    @Nested
    @DisplayName("isAvailableForPawn()")
    class AvailableForPawn {

        @Test
        @DisplayName("a redeemed or in-sale item can be pawned again")
        void redeemedOrInSale_available() {
            assertTrue(item(ItemStatus.REDEEMED).isAvailableForPawn());
            assertTrue(item(ItemStatus.IN_SALE).isAvailableForPawn());
        }

        @Test
        @DisplayName("an in-pawn or sold item cannot be pawned")
        void inPawnOrSold_unavailable() {
            assertFalse(item(ItemStatus.IN_PAWN).isAvailableForPawn());
            assertFalse(item(ItemStatus.SOLD).isAvailableForPawn());
        }
    }

    @Nested
    @DisplayName("attributes")
    class Attributes {

        @Test
        @DisplayName("put and read a typed attribute")
        void putAndRead() {
            Item item = item(ItemStatus.REDEEMED);

            item.putAttribute("carats", 18);

            assertEquals(18, item.attribute("carats"));
        }

        @Test
        @DisplayName("describe sets the description")
        void describe() {
            Item item = item(ItemStatus.REDEEMED);

            item.describe("22k gold ring");

            assertEquals("22k gold ring", item.getDescription());
        }
    }
}
