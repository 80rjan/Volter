package com.volter.shop.sale;

import com.volter.shop.modules.customer.domain.model.Customer;
import com.volter.shop.modules.customer.infrastructure.CustomerMapperImpl;
import com.volter.shop.modules.inventory.application.dtos.types.electronic.response.ElectronicItemResponseData;
import com.volter.shop.modules.inventory.application.dtos.types.gold.response.GoldItemDetailedResponseData;
import com.volter.shop.modules.inventory.application.dtos.types.gold.response.GoldItemResponseData;
import com.volter.shop.modules.inventory.domain.model.Item;
import com.volter.shop.modules.inventory.domain.model.types.ElectronicItem;
import com.volter.shop.modules.inventory.domain.model.types.GoldItem;
import com.volter.shop.modules.inventory.infrastructure.ItemDetailedMapperImpl;
import com.volter.shop.modules.inventory.infrastructure.ItemMapperImpl;
import com.volter.shop.modules.sale.application.dto.response.SaleDetailedResponse;
import com.volter.shop.modules.sale.application.dto.response.SaleResponse;
import com.volter.shop.modules.sale.domain.model.Sale;
import com.volter.shop.modules.sale.infrastructure.SaleMapper;
import com.volter.shop.modules.sale.infrastructure.SaleMapperImpl;
import com.volter.shop.shared.valueobject.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {
        SaleMapperImpl.class,
        ItemMapperImpl.class,
        ItemDetailedMapperImpl.class,
        CustomerMapperImpl.class
})
class SaleMapperTest {

    @Autowired
    private SaleMapper saleMapper;

    // ─── fixtures ────────────────────────────────────────────────────────────

    private Customer customer() {
        return Customer.builder()
                .name("Test Customer")
                .phoneNumber("070111111")
                .embg("1234567890123")
                .address("Street 1")
                .city("Skopje")
                .build();
    }

    private GoldItem goldItem() {
        return GoldItem.builder().build();
    }

    private ElectronicItem electronicItem() {
        return ElectronicItem.builder().build();
    }

    private Sale sale(Item item) {
        return Sale.builder()
                .purchasePrice(new Money(2000))
                .customer(customer())
                .item(item)
                .build();
    }

    // =========================================================================
    // toResponse() — base response
    // =========================================================================

    @Nested
    @DisplayName("toResponse() — base response")
    class ToResponse {

        @Test
        @DisplayName("maps purchasePrice from Money to Integer")
        void mapsPurchasePrice_fromMoneyToInteger() {
            assertThat(saleMapper.toResponse(sale(goldItem())).getPurchasePrice()).isEqualTo(2000);
        }

        @Test
        @DisplayName("item is not null")
        void item_isNotNull() {
            assertThat(saleMapper.toResponse(sale(goldItem())).getItem()).isNotNull();
        }

        @Test
        @DisplayName("GoldItem sale maps item to GoldItemResponseData")
        void goldSale_mapsItemToGoldItemResponseData() {
            assertThat(saleMapper.toResponse(sale(goldItem())).getItem())
                    .isInstanceOf(GoldItemResponseData.class);
        }

        @Test
        @DisplayName("ElectronicItem sale maps item to ElectronicItemResponseData")
        void electronicSale_mapsItemToElectronicItemResponseData() {
            assertThat(saleMapper.toResponse(sale(electronicItem())).getItem())
                    .isInstanceOf(ElectronicItemResponseData.class);
        }

        @Test
        @DisplayName("null purchasePrice maps to null Integer")
        void nullPurchasePrice_mapsToNull() {
            Sale s = Sale.builder().purchasePrice(null).customer(customer()).item(goldItem()).build();
            assertThat(saleMapper.toResponse(s).getPurchasePrice()).isNull();
        }
    }

    // =========================================================================
    // toDetailedResponse() — detailed response
    // =========================================================================

    @Nested
    @DisplayName("toDetailedResponse() — detailed response")
    class ToDetailedResponse {

        @Test
        @DisplayName("maps purchasePrice from Money to Integer")
        void mapsPurchasePrice() {
            assertThat(saleMapper.toDetailedResponse(sale(goldItem())).getPurchasePrice()).isEqualTo(2000);
        }

        @Test
        @DisplayName("maps full customer object, not just ID")
        void mapsFullCustomer() {
            SaleDetailedResponse r = saleMapper.toDetailedResponse(sale(goldItem()));
            assertThat(r.getCustomer()).isNotNull();
            assertThat(r.getCustomer().getName()).isEqualTo("Test Customer");
        }

        @Test
        @DisplayName("GoldItem sale maps item to GoldItemDetailedResponseData")
        void goldSale_mapsItemToDetailedResponseData() {
            assertThat(saleMapper.toDetailedResponse(sale(goldItem())).getItem())
                    .isInstanceOf(GoldItemDetailedResponseData.class);
        }

        @Test
        @DisplayName("createdAt and updatedAt are null when @PrePersist has not fired")
        void timestamps_areNullWithoutPrePersist() {
            SaleDetailedResponse r = saleMapper.toDetailedResponse(sale(goldItem()));
            assertThat(r.getCreatedAt()).isNull();
            assertThat(r.getUpdatedAt()).isNull();
        }
    }

    // =========================================================================
    // toResponse vs toDetailedResponse — item mapper wiring
    // =========================================================================

    @Nested
    @DisplayName("toResponse vs toDetailedResponse — item mapper wiring")
    class ItemMapperWiring {

        @Test
        @DisplayName("toResponse uses base ItemMapper, toDetailedResponse uses ItemDetailedMapper")
        void responseAndDetailedResponse_useDifferentItemMappers() {
            Sale goldSale = sale(goldItem());
            assertThat(saleMapper.toResponse(goldSale).getItem())
                    .isInstanceOf(GoldItemResponseData.class)
                    .isNotInstanceOf(GoldItemDetailedResponseData.class);
            assertThat(saleMapper.toDetailedResponse(goldSale).getItem())
                    .isInstanceOf(GoldItemDetailedResponseData.class);
        }
    }

    // =========================================================================
    // List overloads
    // =========================================================================

    @Nested
    @DisplayName("List overloads")
    class ListOverloads {

        @Test
        @DisplayName("toResponse(List) returns correct size")
        void toResponseList_correctSize() {
            List<SaleResponse> results = saleMapper.toResponse(
                    List.of(sale(goldItem()), sale(electronicItem())));
            assertThat(results).hasSize(2);
        }

        @Test
        @DisplayName("toResponse(List) with empty list returns empty list")
        void toResponseList_emptyList() {
            assertThat(saleMapper.toResponse(List.of())).isEmpty();
        }

        @Test
        @DisplayName("toDetailedResponse(List) returns correct size")
        void toDetailedResponseList_correctSize() {
            List<SaleDetailedResponse> results =
                    saleMapper.toDetailedResponse(List.of(sale(goldItem())));
            assertThat(results).hasSize(1);
            assertThat(results.get(0).getPurchasePrice()).isEqualTo(2000);
        }
    }
}
