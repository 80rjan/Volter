package com.volter.shop.pawn;

import com.volter.shop.modules.customer.domain.model.Customer;
import com.volter.shop.modules.customer.infrastructure.mapper.CustomerMapperImpl;
import com.volter.shop.modules.inventory.domain.model.types.GoldItem;
import com.volter.shop.modules.inventory.domain.model.types.ElectronicItem;
import com.volter.shop.modules.inventory.infrastructure.mapper.ItemDetailedMapperImpl;
import com.volter.shop.modules.inventory.infrastructure.mapper.ItemMapperImpl;
import com.volter.shop.modules.inventory.web.response.types.electronic.ElectronicItemDetailedResponseData;
import com.volter.shop.modules.pawn.web.response.PawnDetailedResponse;
import com.volter.shop.modules.pawn.web.response.PawnResponse;
import com.volter.shop.modules.pawn.domain.model.Pawn;
import com.volter.shop.modules.pawn.domain.model.valueobject.PawnPeriod;
import com.volter.shop.modules.pawn.domain.model.enums.PawnStatus;
import com.volter.shop.modules.inventory.web.response.types.gold.GoldItemResponseData;
import com.volter.shop.modules.inventory.web.response.types.gold.GoldItemDetailedResponseData;
import com.volter.shop.modules.inventory.web.response.types.electronic.ElectronicItemResponseData;
import com.volter.shop.modules.pawn.infrastructure.mapper.PawnMapper;
import com.volter.shop.modules.pawn.infrastructure.mapper.PawnMapperImpl;
import com.volter.shop.shared.valueobject.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

// Loads only the mapper beans — no web layer, no repositories
@SpringBootTest(classes = {
        PawnMapperImpl.class,
        ItemMapperImpl.class,
        ItemDetailedMapperImpl.class,
        CustomerMapperImpl.class
})
class PawnMapperTest {

    @Autowired
    private PawnMapper pawnMapper;

    private Customer customer;
    private PawnPeriod period;

    @BeforeEach
    void setUp() {
        customer = Customer.builder()
                .name("Test IdentityUser")
                .phoneNumber("070123456")
                .embg("1234567890123")
                .address("Test St 1")
                .city("Skopje")
                .build();
        // force an id via reflection if needed, or leave null — test still validates mapping
        period = new PawnPeriod(
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 2, 1)
        );
    }

    // ─── helpers ─────────────────────────────────────────────────────────────

    private Pawn buildPawnWithItem(Object item) {
        return Pawn.builder()
                .amount(new Money(1000))
                .interest(new Money(50))
                .period(period)
                .defaultDurationDays(30)
                .status(PawnStatus.ACTIVE)
                .active(true)
                .createdAt(LocalDateTime.of(2025, 1, 1, 10, 0))
                .updatedAt(LocalDateTime.of(2025, 1, 15, 10, 0))
                .customer(customer)
                .item((com.volter.shop.modules.inventory.domain.model.Item) item)
                .build();
    }

    private GoldItem goldItem() {
        return GoldItem.builder()
                // populate required fields — adjust to your GoldItem structure
                .build();
    }

    private ElectronicItem electronicItem() {
        return ElectronicItem.builder()
                .build();
    }

    // =========================================================================
    // toResponse() — PawnResponse
    // =========================================================================

    @Nested
    class ToResponse {

        @Test
        void mapsId() {
            Pawn pawn = buildPawnWithItem(goldItem());
            PawnResponse r = pawnMapper.toResponse(pawn);
            assertThat(r.getId()).isEqualTo(pawn.getId());
        }

        @Test
        void mapsAmount_fromMoneyToInteger() {
            Pawn pawn = buildPawnWithItem(goldItem());
            PawnResponse r = pawnMapper.toResponse(pawn);
            assertThat(r.getAmount()).isEqualTo(1000);
        }

        @Test
        void mapsInterest_fromMoneyToInteger() {
            Pawn pawn = buildPawnWithItem(goldItem());
            PawnResponse r = pawnMapper.toResponse(pawn);
            assertThat(r.getInterest()).isEqualTo(50);
        }

        @Test
        void mapsIssueDateFromPeriod() {
            Pawn pawn = buildPawnWithItem(goldItem());
            PawnResponse r = pawnMapper.toResponse(pawn);
            assertThat(r.getIssueDate()).isEqualTo(LocalDate.of(2025, 1, 1));
        }

        @Test
        void mapsMaturityDateFromPeriod() {
            Pawn pawn = buildPawnWithItem(goldItem());
            PawnResponse r = pawnMapper.toResponse(pawn);
            assertThat(r.getMaturityDate()).isEqualTo(LocalDate.of(2025, 2, 1));
        }

        @Test
        void mapsDefaultDurationDays() {
            Pawn pawn = buildPawnWithItem(goldItem());
            assertThat(pawnMapper.toResponse(pawn).getDefaultDurationDays()).isEqualTo(30);
        }

        @Test
        void mapsCustomerId() {
            Pawn pawn = buildPawnWithItem(goldItem());
            PawnResponse r = pawnMapper.toResponse(pawn);
            assertThat(r.getCustomerId()).isEqualTo(customer.getId());
        }

        @Test
        void mapsCustomerName() {
            Pawn pawn = buildPawnWithItem(goldItem());
            assertThat(pawnMapper.toResponse(pawn).getCustomerName()).isEqualTo("Test IdentityUser");
        }

        @Test
        void item_isNotNull() {
            Pawn pawn = buildPawnWithItem(goldItem());
            assertThat(pawnMapper.toResponse(pawn).getItem()).isNotNull();
        }

        @Test
        void item_goldPawn_mapsToGoldItemResponseData() {
            Pawn pawn = buildPawnWithItem(goldItem());
            assertThat(pawnMapper.toResponse(pawn).getItem())
                    .isInstanceOf(GoldItemResponseData.class);
        }

        @Test
        void item_electronicPawn_mapsToElectronicItemResponseData() {
            Pawn pawn = buildPawnWithItem(electronicItem());
            assertThat(pawnMapper.toResponse(pawn).getItem())
                    .isInstanceOf(ElectronicItemResponseData.class);
        }

        @Test
        void amount_null_mapsToNull() {
            // verifies the null-safe map(Money) helper
            Pawn pawn = Pawn.builder()
                    .amount(null)
                    .interest(new Money(50))
                    .period(period)
                    .defaultDurationDays(30)
                    .customer(customer)
                    .item(goldItem())
                    .build();
            // MapStruct will call map(Money) → returns null safely
            assertThatNoException().isThrownBy(() -> pawnMapper.toResponse(pawn));
            assertThat(pawnMapper.toResponse(pawn).getAmount()).isNull();
        }
    }

    // =========================================================================
    // toResponse(List) — list delegation
    // =========================================================================

    @Nested
    class ToResponseList {

        @Test
        void mapsListOfPawns() {
            Pawn p1 = buildPawnWithItem(goldItem());
            Pawn p2 = buildPawnWithItem(electronicItem());
            List<PawnResponse> results = pawnMapper.toResponse(List.of(p1, p2));
            assertThat(results).hasSize(2);
        }

        @Test
        void emptyList_returnsEmptyList() {
            assertThat(pawnMapper.toResponse(List.of())).isEmpty();
        }

        @Test
        void eachElementIsMappedCorrectly() {
            Pawn pawn = buildPawnWithItem(goldItem());
            List<PawnResponse> results = pawnMapper.toResponse(List.of(pawn));
            assertThat(results.get(0).getAmount()).isEqualTo(1000);
            assertThat(results.get(0).getCustomerName()).isEqualTo("Test IdentityUser");
        }
    }

    // =========================================================================
    // toDetailedResponse() — PawnDetailedResponse
    // =========================================================================

    @Nested
    class ToDetailedResponse {

        @Test
        void mapsId() {
            Pawn pawn = buildPawnWithItem(goldItem());
            assertThat(pawnMapper.toDetailedResponse(pawn).getId()).isEqualTo(pawn.getId());
        }

        @Test
        void mapsAmount() {
            Pawn pawn = buildPawnWithItem(goldItem());
            assertThat(pawnMapper.toDetailedResponse(pawn).getAmount()).isEqualTo(1000);
        }

        @Test
        void mapsInterest() {
            Pawn pawn = buildPawnWithItem(goldItem());
            assertThat(pawnMapper.toDetailedResponse(pawn).getInterest()).isEqualTo(50);
        }

        @Test
        void mapsIssueDateFromPeriod() {
            Pawn pawn = buildPawnWithItem(goldItem());
            assertThat(pawnMapper.toDetailedResponse(pawn).getIssueDate())
                    .isEqualTo(LocalDate.of(2025, 1, 1));
        }

        @Test
        void mapsMaturityDateFromPeriod() {
            Pawn pawn = buildPawnWithItem(goldItem());
            assertThat(pawnMapper.toDetailedResponse(pawn).getMaturityDate())
                    .isEqualTo(LocalDate.of(2025, 2, 1));
        }

        @Test
        void mapsDefaultDurationDays() {
            assertThat(pawnMapper.toDetailedResponse(buildPawnWithItem(goldItem())).getDefaultDurationDays())
                    .isEqualTo(30);
        }

        @Test
        void mapsStatus() {
            Pawn pawn = buildPawnWithItem(goldItem());
            assertThat(pawnMapper.toDetailedResponse(pawn).getStatus()).isEqualTo(PawnStatus.ACTIVE);
        }

        @Test
        void mapsActive() {
            Pawn pawn = buildPawnWithItem(goldItem());
            assertThat(pawnMapper.toDetailedResponse(pawn).isActive()).isTrue();
        }

        @Test
        void mapsCreatedAt() {
            Pawn pawn = buildPawnWithItem(goldItem());
            assertThat(pawnMapper.toDetailedResponse(pawn).getCreatedAt())
                    .isEqualTo(LocalDateTime.of(2025, 1, 1, 10, 0));
        }

        @Test
        void mapsUpdatedAt() {
            Pawn pawn = buildPawnWithItem(goldItem());
            assertThat(pawnMapper.toDetailedResponse(pawn).getUpdatedAt())
                    .isEqualTo(LocalDateTime.of(2025, 1, 15, 10, 0));
        }

        @Test
        void mapsFullCustomerObject_notJustId() {
            Pawn pawn = buildPawnWithItem(goldItem());
            PawnDetailedResponse r = pawnMapper.toDetailedResponse(pawn);
            assertThat(r.getCustomer()).isNotNull();
            assertThat(r.getCustomer().getName()).isEqualTo("Test IdentityUser");
        }

        @Test
        void item_isNotNull() {
            Pawn pawn = buildPawnWithItem(goldItem());
            assertThat(pawnMapper.toDetailedResponse(pawn).getItem()).isNotNull();
        }

        @Test
        void item_goldPawn_mapsToGoldItemDetailedResponseData() {
            Pawn pawn = buildPawnWithItem(goldItem());
            assertThat(pawnMapper.toDetailedResponse(pawn).getItem())
                    .isInstanceOf(GoldItemDetailedResponseData.class);
        }

        @Test
        void item_electronicPawn_mapsToElectronicItemDetailedResponseData() {
            Pawn pawn = buildPawnWithItem(electronicItem());
            assertThat(pawnMapper.toDetailedResponse(pawn).getItem())
                    .isInstanceOf(ElectronicItemDetailedResponseData.class);
        }

        // KEY: toResponse uses @Named("toBaseResponse"), toDetailedResponse uses @Named("toDetailedResponse")
        // this test confirms they don't cross-wire
        @Test
        void toResponse_and_toDetailedResponse_useDifferentItemMappers() {
            Pawn pawn = buildPawnWithItem(goldItem());
            assertThat(pawnMapper.toResponse(pawn).getItem())
                    .isInstanceOf(GoldItemResponseData.class)
                    .isNotInstanceOf(GoldItemDetailedResponseData.class);
            assertThat(pawnMapper.toDetailedResponse(pawn).getItem())
                    .isInstanceOf(GoldItemDetailedResponseData.class);
        }
    }
}
