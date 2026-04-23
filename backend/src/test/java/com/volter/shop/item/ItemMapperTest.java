package com.volter.shop.item;

import com.volter.shop.modules.inventory.application.dtos.baseitem.response.ItemDetailedResponseData;
import com.volter.shop.modules.inventory.application.dtos.baseitem.response.ItemResponseData;
import com.volter.shop.modules.inventory.application.dtos.types.electronic.response.ElectronicItemDetailedResponseData;
import com.volter.shop.modules.inventory.application.dtos.types.electronic.response.ElectronicItemResponseData;
import com.volter.shop.modules.inventory.application.dtos.types.gold.response.GoldItemDetailedResponseData;
import com.volter.shop.modules.inventory.application.dtos.types.gold.response.GoldItemResponseData;
import com.volter.shop.modules.inventory.application.dtos.types.other.response.OtherItemDetailedResponseData;
import com.volter.shop.modules.inventory.application.dtos.types.other.response.OtherItemResponseData;
import com.volter.shop.modules.inventory.application.dtos.types.vehicle.response.VehicleItemDetailedResponseData;
import com.volter.shop.modules.inventory.application.dtos.types.vehicle.response.VehicleItemResponseData;
import com.volter.shop.modules.inventory.application.dtos.types.watch.WatchItemDetailedResponseData;
import com.volter.shop.modules.inventory.application.dtos.types.watch.WatchItemResponseData;
import com.volter.shop.modules.inventory.domain.model.enums.ItemOriginType;
import com.volter.shop.modules.inventory.domain.model.enums.ItemStatus;
import com.volter.shop.modules.inventory.domain.model.enums.types.GoldItemCarats;
import com.volter.shop.modules.inventory.domain.model.enums.types.VehicleItemVehicleType;
import com.volter.shop.modules.inventory.domain.model.types.*;
import com.volter.shop.modules.inventory.infrastructure.ItemDetailedMapper;
import com.volter.shop.modules.inventory.infrastructure.ItemMapper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ItemMapperTest.MapperConfig.class)
class ItemMapperTest {

    @Configuration
    @ComponentScan(basePackages = "com.volter.shop.modules.inventory.infrastructure")
    static class MapperConfig {}

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private ItemDetailedMapper itemDetailedMapper;

    // ─── builders ────────────────────────────────────────────────────────────

    private GoldItem goldItem() {
        return GoldItem.builder()
                .description("Gold necklace")
                .itemOriginType(ItemOriginType.PAWN)
                .itemStatus(ItemStatus.IN_PAWN)
                .weightGrams(new BigDecimal("10.50"))
                .pricePerGram(3500)
                .carats(GoldItemCarats.CARAT_18)
                .pieceType("Necklace")
                .build();
    }

    private ElectronicItem electronicItem() {
        return ElectronicItem.builder()
                .description("MacBook Pro")
                .itemOriginType(ItemOriginType.PAWN)
                .itemStatus(ItemStatus.IN_PAWN)
                .brand("Apple")
                .category("Laptop")
                .year(2022)
                .build();
    }

    private WatchItem watchItem() {
        return WatchItem.builder()
                .description("Rolex Submariner")
                .itemOriginType(ItemOriginType.PAWN)
                .itemStatus(ItemStatus.IN_PAWN)
                .brand("Rolex")
                .model("Submariner")
                .material("Steel")
                .year(2020)
                .originalBoxIncluded(true)
                .originalPapersIncluded(true)
                .warrantyCardIncluded(false)
                .warrantyExpirationDate(null)
                .functional(true)
                .serviceRequired(false)
                .build();
    }

    private VehicleItem vehicleItem() {
        return VehicleItem.builder()
                .description("BMW 3 Series")
                .itemOriginType(ItemOriginType.PAWN)
                .itemStatus(ItemStatus.IN_PAWN)
                .brand("BMW")
                .model("320d")
                .vehicleType(VehicleItemVehicleType.SEDAN)
                .year(2019)
                .registrationNumber("SK-1234-AB")
                .mileage(85000)
                .serviceHistoryAvailable(true)
                .lastServiceDate(LocalDate.of(2024, 6, 1))
                .registrationExpiryDate(LocalDate.of(2026, 12, 31))
                .numberOfKeys(2)
                .build();
    }

    private OtherItem otherItem() {
        return OtherItem.builder()
                .description("Vintage camera")
                .itemOriginType(ItemOriginType.PAWN)
                .itemStatus(ItemStatus.IN_PAWN)
                .category("Camera")
                .build();
    }

    // =========================================================================
    // ItemMapper — toResponseData() subclass dispatch
    // =========================================================================

    @Nested
    class ItemMapper_SubclassDispatch {

        @Test
        void goldItem_mapsToGoldItemResponseData() {
            assertThat(itemMapper.toResponseData(goldItem())).isInstanceOf(GoldItemResponseData.class);
        }

        @Test
        void electronicItem_mapsToElectronicItemResponseData() {
            assertThat(itemMapper.toResponseData(electronicItem())).isInstanceOf(ElectronicItemResponseData.class);
        }

        @Test
        void watchItem_mapsToWatchItemResponseData() {
            assertThat(itemMapper.toResponseData(watchItem())).isInstanceOf(WatchItemResponseData.class);
        }

        @Test
        void vehicleItem_mapsToVehicleItemResponseData() {
            assertThat(itemMapper.toResponseData(vehicleItem())).isInstanceOf(VehicleItemResponseData.class);
        }

        @Test
        void otherItem_mapsToOtherItemResponseData() {
            assertThat(itemMapper.toResponseData(otherItem())).isInstanceOf(OtherItemResponseData.class);
        }

        @Test
        void unknownSubclass_throwsRuntimeException() {
            // SubclassExhaustiveStrategy.RUNTIME_EXCEPTION — unknown type must throw
            class UnknownItem extends com.volter.shop.modules.inventory.domain.model.Item {}
            assertThatThrownBy(() -> itemMapper.toResponseData(new UnknownItem()))
                    .isInstanceOf(RuntimeException.class);
        }

        @Test
        void null_returnsNull() {
            assertThat(itemMapper.toResponseData(null)).isNull();
        }
    }

    // =========================================================================
    // ItemMapper — GoldItem field mapping
    // =========================================================================

    @Nested
    class ItemMapper_Gold {

        @Test
        void mapsDescription() {
            GoldItemResponseData r = (GoldItemResponseData) itemMapper.toResponseData(goldItem());
            assertThat(r.getDescription()).isEqualTo("Gold necklace");
        }

        @Test
        void mapsWeightGrams() {
            GoldItemResponseData r = (GoldItemResponseData) itemMapper.toResponseData(goldItem());
            assertThat(r.getWeightGrams()).isEqualByComparingTo(new BigDecimal("10.50"));
        }

        @Test
        void mapsCarats() {
            GoldItemResponseData r = (GoldItemResponseData) itemMapper.toResponseData(goldItem());
            assertThat(r.getCarats()).isEqualTo(GoldItemCarats.CARAT_18);
        }

        @Test
        void mapsPieceType() {
            GoldItemResponseData r = (GoldItemResponseData) itemMapper.toResponseData(goldItem());
            assertThat(r.getPieceType()).isEqualTo("Necklace");
        }

        @Test
        void itemTypeIsGold() {
            assertThat(itemMapper.toResponseData(goldItem()).getItemType())
                    .isEqualTo(com.volter.shop.modules.inventory.domain.model.enums.ItemType.GOLD);
        }
    }

    // =========================================================================
    // ItemMapper — ElectronicItem field mapping
    // =========================================================================

    @Nested
    class ItemMapper_Electronic {

        @Test
        void mapsDescription() {
            ElectronicItemResponseData r = (ElectronicItemResponseData) itemMapper.toResponseData(electronicItem());
            assertThat(r.getDescription()).isEqualTo("MacBook Pro");
        }

        @Test
        void mapsBrand() {
            ElectronicItemResponseData r = (ElectronicItemResponseData) itemMapper.toResponseData(electronicItem());
            assertThat(r.getBrand()).isEqualTo("Apple");
        }

        @Test
        void mapsCategory() {
            ElectronicItemResponseData r = (ElectronicItemResponseData) itemMapper.toResponseData(electronicItem());
            assertThat(r.getCategory()).isEqualTo("Laptop");
        }

        @Test
        void mapsYear() {
            ElectronicItemResponseData r = (ElectronicItemResponseData) itemMapper.toResponseData(electronicItem());
            assertThat(r.getYear()).isEqualTo(2022);
        }
    }

    // =========================================================================
    // ItemMapper — WatchItem field mapping
    // =========================================================================

    @Nested
    class ItemMapper_Watch {

        @Test
        void mapsAllBaseFields() {
            WatchItemResponseData r = (WatchItemResponseData) itemMapper.toResponseData(watchItem());
            assertThat(r.getBrand()).isEqualTo("Rolex");
            assertThat(r.getModel()).isEqualTo("Submariner");
            assertThat(r.getMaterial()).isEqualTo("Steel");
            assertThat(r.getYear()).isEqualTo(2020);
            assertThat(r.getDescription()).isEqualTo("Rolex Submariner");
        }
    }

    // =========================================================================
    // ItemMapper — VehicleItem field mapping
    // =========================================================================

    @Nested
    class ItemMapper_Vehicle {

        @Test
        void mapsAllBaseFields() {
            VehicleItemResponseData r = (VehicleItemResponseData) itemMapper.toResponseData(vehicleItem());
            assertThat(r.getBrand()).isEqualTo("BMW");
            assertThat(r.getModel()).isEqualTo("320d");
            assertThat(r.getYear()).isEqualTo(2019);
            assertThat(r.getRegistrationNumber()).isEqualTo("SK-1234-AB");
        }
    }

    // =========================================================================
    // ItemMapper — OtherItem field mapping
    // =========================================================================

    @Nested
    class ItemMapper_Other {

        @Test
        void mapsDescriptionAndCategory() {
            OtherItemResponseData r = (OtherItemResponseData) itemMapper.toResponseData(otherItem());
            assertThat(r.getDescription()).isEqualTo("Vintage camera");
            assertThat(r.getCategory()).isEqualTo("Camera");
        }
    }

    // =========================================================================
    // ItemDetailedMapper — subclass dispatch
    // =========================================================================

    @Nested
    class ItemDetailedMapper_SubclassDispatch {

        @Test
        void goldItem_mapsToGoldItemDetailedResponseData() {
            assertThat(itemDetailedMapper.toDetailedResponseData(goldItem()))
                    .isInstanceOf(GoldItemDetailedResponseData.class);
        }

        @Test
        void electronicItem_mapsToElectronicItemDetailedResponseData() {
            assertThat(itemDetailedMapper.toDetailedResponseData(electronicItem()))
                    .isInstanceOf(ElectronicItemDetailedResponseData.class);
        }

        @Test
        void watchItem_mapsToWatchItemDetailedResponseData() {
            assertThat(itemDetailedMapper.toDetailedResponseData(watchItem()))
                    .isInstanceOf(WatchItemDetailedResponseData.class);
        }

        @Test
        void vehicleItem_mapsToVehicleItemDetailedResponseData() {
            assertThat(itemDetailedMapper.toDetailedResponseData(vehicleItem()))
                    .isInstanceOf(VehicleItemDetailedResponseData.class);
        }

        @Test
        void otherItem_mapsToOtherItemDetailedResponseData() {
            assertThat(itemDetailedMapper.toDetailedResponseData(otherItem()))
                    .isInstanceOf(OtherItemDetailedResponseData.class);
        }

        @Test
        void null_returnsNull() {
            assertThat(itemDetailedMapper.toDetailedResponseData(null)).isNull();
        }
    }

    // =========================================================================
    // ItemDetailedMapper — Gold detailed fields
    // =========================================================================

    @Nested
    class ItemDetailedMapper_Gold {

        @Test
        void mapsInheritedBaseFields() {
            GoldItemDetailedResponseData r = (GoldItemDetailedResponseData)
                    itemDetailedMapper.toDetailedResponseData(goldItem());
            assertThat(r.getDescription()).isEqualTo("Gold necklace");
            assertThat(r.getWeightGrams()).isEqualByComparingTo(new BigDecimal("10.50"));
            assertThat(r.getCarats()).isEqualTo(GoldItemCarats.CARAT_18);
            assertThat(r.getPieceType()).isEqualTo("Necklace");
        }

        @Test
        void mapsDetailedOnlyFields() {
            GoldItemDetailedResponseData r = (GoldItemDetailedResponseData)
                    itemDetailedMapper.toDetailedResponseData(goldItem());
            assertThat(r.getItemStatus()).isEqualTo(ItemStatus.IN_PAWN);
            assertThat(r.getPricePerGram()).isEqualTo(3500);
        }

        @Test
        void createdAt_and_updatedAt_areNullWhenPrePersistNotFired() {
            // @PrePersist not called in unit test — these will be null without DB
            GoldItemDetailedResponseData r = (GoldItemDetailedResponseData)
                    itemDetailedMapper.toDetailedResponseData(goldItem());
            assertThat(r.getCreatedAt()).isNull();
            assertThat(r.getUpdatedAt()).isNull();
        }
    }

    // =========================================================================
    // ItemDetailedMapper — Electronic detailed fields
    // =========================================================================

    @Nested
    class ItemDetailedMapper_Electronic {

        @Test
        void mapsInheritedAndDetailedFields() {
            ElectronicItemDetailedResponseData r = (ElectronicItemDetailedResponseData)
                    itemDetailedMapper.toDetailedResponseData(electronicItem());
            assertThat(r.getBrand()).isEqualTo("Apple");
            assertThat(r.getCategory()).isEqualTo("Laptop");
            assertThat(r.getYear()).isEqualTo(2022);
            assertThat(r.getItemStatus()).isEqualTo(ItemStatus.IN_PAWN);
        }
    }

    // =========================================================================
    // ItemDetailedMapper — Watch detailed fields
    // =========================================================================

    @Nested
    class ItemDetailedMapper_Watch {

        @Test
        void mapsDetailedWatchFields() {
            WatchItemDetailedResponseData r = (WatchItemDetailedResponseData)
                    itemDetailedMapper.toDetailedResponseData(watchItem());
            assertThat(r.isOriginalBoxIncluded()).isTrue();
            assertThat(r.isOriginalPapersIncluded()).isTrue();
            assertThat(r.isWarrantyCardIncluded()).isFalse();
            assertThat(r.isFunctional()).isTrue();
            assertThat(r.isServiceRequired()).isFalse();
            assertThat(r.getWarrantyExpirationDate()).isNull();
        }

        @Test
        void mapsInheritedFields() {
            WatchItemDetailedResponseData r = (WatchItemDetailedResponseData)
                    itemDetailedMapper.toDetailedResponseData(watchItem());
            assertThat(r.getBrand()).isEqualTo("Rolex");
            assertThat(r.getModel()).isEqualTo("Submariner");
            assertThat(r.getMaterial()).isEqualTo("Steel");
            assertThat(r.getItemStatus()).isEqualTo(ItemStatus.IN_PAWN);
        }
    }

    // =========================================================================
    // ItemDetailedMapper — Vehicle detailed fields
    // =========================================================================

    @Nested
    class ItemDetailedMapper_Vehicle {

        @Test
        void mapsDetailedVehicleFields() {
            VehicleItemDetailedResponseData r = (VehicleItemDetailedResponseData)
                    itemDetailedMapper.toDetailedResponseData(vehicleItem());
            assertThat(r.getVehicleType()).isEqualTo(VehicleItemVehicleType.SEDAN);
            assertThat(r.getMileage()).isEqualTo(85000);
            assertThat(r.isServiceHistoryAvailable()).isTrue();
            assertThat(r.getLastServiceDate()).isEqualTo(LocalDate.of(2024, 6, 1));
            assertThat(r.getRegistrationExpiryDate()).isEqualTo(LocalDate.of(2026, 12, 31));
            assertThat(r.getNumberOfKeys()).isEqualTo(2);
        }

        @Test
        void mapsInheritedFields() {
            VehicleItemDetailedResponseData r = (VehicleItemDetailedResponseData)
                    itemDetailedMapper.toDetailedResponseData(vehicleItem());
            assertThat(r.getBrand()).isEqualTo("BMW");
            assertThat(r.getModel()).isEqualTo("320d");
            assertThat(r.getRegistrationNumber()).isEqualTo("SK-1234-AB");
            assertThat(r.getItemStatus()).isEqualTo(ItemStatus.IN_PAWN);
        }

        @Test
        void nullLastServiceDate_isMappedAsNull() {
            VehicleItem v = VehicleItem.builder()
                    .description("Test car")
                    .itemOriginType(ItemOriginType.PAWN)
                    .itemStatus(ItemStatus.IN_PAWN)
                    .brand("Toyota").model("Yaris")
                    .vehicleType(VehicleItemVehicleType.SEDAN)
                    .year(2018).registrationNumber("SK-0000-ZZ")
                    .mileage(50000).serviceHistoryAvailable(false)
                    .lastServiceDate(null)
                    .registrationExpiryDate(LocalDate.of(2025, 1, 1))
                    .numberOfKeys(1)
                    .build();
            VehicleItemDetailedResponseData r = (VehicleItemDetailedResponseData)
                    itemDetailedMapper.toDetailedResponseData(v);
            assertThat(r.getLastServiceDate()).isNull();
        }
    }

    // =========================================================================
    // ItemDetailedMapper — Other detailed fields
    // =========================================================================

    @Nested
    class ItemDetailedMapper_Other {

        @Test
        void mapsAllFields() {
            OtherItemDetailedResponseData r = (OtherItemDetailedResponseData)
                    itemDetailedMapper.toDetailedResponseData(otherItem());
            assertThat(r.getDescription()).isEqualTo("Vintage camera");
            assertThat(r.getCategory()).isEqualTo("Camera");
            assertThat(r.getItemStatus()).isEqualTo(ItemStatus.IN_PAWN);
        }
    }

    // =========================================================================
    // Cross-mapper — base vs detailed never cross-wire
    // =========================================================================

    @Nested
    class CrossMapper {

        @Test
        void itemMapper_neverReturnsDetailedResponse() {
            ItemResponseData base = itemMapper.toResponseData(goldItem());
            assertThat(base).isNotInstanceOf(ItemDetailedResponseData.class);
        }

        @Test
        void itemDetailedMapper_returnsDetailedResponse() {
            ItemDetailedResponseData detailed = itemDetailedMapper.toDetailedResponseData(goldItem());
            assertThat(detailed).isInstanceOf(ItemDetailedResponseData.class);
        }

        @Test
        void sameGoldItem_baseHasNoStatusField_detailedHas() {
            GoldItem item = goldItem();
            GoldItemResponseData base     = (GoldItemResponseData) itemMapper.toResponseData(item);
            GoldItemDetailedResponseData detailed = (GoldItemDetailedResponseData) itemDetailedMapper.toDetailedResponseData(item);
            // base response has no itemStatus — confirmed by not being an instance of detailed
            assertThat(base).isNotInstanceOf(GoldItemDetailedResponseData.class);
            assertThat(detailed.getItemStatus()).isNotNull();
        }
    }
}
