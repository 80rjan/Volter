package com.volter.shop.item;

import com.volter.shop.modules.inventory.application.ItemService;
import com.volter.shop.modules.inventory.application.dto.ItemCreateRequest;
import com.volter.shop.modules.inventory.domain.model.Item;
import com.volter.shop.modules.inventory.domain.model.ItemStatusHistory;
import com.volter.shop.modules.inventory.domain.model.enums.ItemOriginType;
import com.volter.shop.modules.inventory.domain.model.enums.ItemStatus;
import com.volter.shop.modules.inventory.domain.model.enums.ItemType;
import com.volter.shop.modules.inventory.domain.repository.ItemRepository;
import com.volter.shop.modules.inventory.domain.repository.ItemStatusHistoryRepository;
import com.volter.shared.web.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemStatusHistoryRepository historyRepository;

    @InjectMocks
    private ItemService itemService;

    private Item item(ItemStatus status) {
        return Item.builder()
                .type(ItemType.GOLD)
                .origin(ItemOriginType.PAWN)
                .status(status)
                .attributes(new HashMap<>())
                .build();
    }

    @Test
    @DisplayName("create persists the item with its initial status")
    void create_persistsItem() {
        ItemCreateRequest request = new ItemCreateRequest(
                ItemType.GOLD, ItemOriginType.PAWN, ItemStatus.IN_PAWN, "ring", null);
        when(itemRepository.save(any(Item.class))).thenAnswer(inv -> inv.getArgument(0));

        Item created = itemService.create(request);

        assertEquals(ItemType.GOLD, created.getType());
        assertEquals(ItemStatus.IN_PAWN, created.getStatus());
        verify(itemRepository).save(any(Item.class));
        verifyNoInteractions(historyRepository);
    }

    @Test
    @DisplayName("markInSale transitions a held item and records the history")
    void markInSale_transitionsAndRecords() {
        Item item = item(ItemStatus.REDEEMED);

        itemService.markInSale(item, 8L);

        assertEquals(ItemStatus.IN_SALE, item.getStatus());
        ArgumentCaptor<ItemStatusHistory> captor = ArgumentCaptor.forClass(ItemStatusHistory.class);
        verify(historyRepository).save(captor.capture());
        assertEquals(ItemStatus.REDEEMED, captor.getValue().getFromStatus());
        assertEquals(ItemStatus.IN_SALE, captor.getValue().getToStatus());
    }

    @Test
    @DisplayName("markSold and markRedeemed each persist their transition")
    void markSoldAndRedeemed_persist() {
        Item sold = item(ItemStatus.IN_SALE);
        itemService.markSold(sold, 8L);
        assertEquals(ItemStatus.SOLD, sold.getStatus());

        Item redeemed = item(ItemStatus.IN_PAWN);
        itemService.markRedeemed(redeemed, 8L);
        assertEquals(ItemStatus.REDEEMED, redeemed.getStatus());

        verify(historyRepository, times(2)).save(any(ItemStatusHistory.class));
    }

    @Test
    @DisplayName("get throws when the item is missing")
    void get_missing_throws() {
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> itemService.get(99L));
    }
}
