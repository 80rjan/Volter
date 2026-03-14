package com.volter.backend.item;

import com.volter.backend.electronicItem.dto.ElectronicItemDetailsResponse;
import com.volter.backend.electronicItem.mapper.ElectronicItemDetailsMapper;
import com.volter.backend.goldItem.dto.GoldItemDetailsResponse;
import com.volter.backend.goldItem.mapper.GoldItemDetailsMapper;
import com.volter.backend.item.dto.ItemDetailsResponse;
import com.volter.backend.item.enums.ItemType;
import com.volter.backend.item.mapper.ItemMapper;
import com.volter.backend.item.mapper.ItemMapperImpl;
import com.volter.backend.otherItem.dto.OtherItemDetailsResponse;
import com.volter.backend.otherItem.mapper.OtherItemDetailsMapper;
import com.volter.backend.vehicleItem.dto.VehicleItemDetailsResponse;
import com.volter.backend.vehicleItem.mapper.VehicleItemDetailsMapper;
import com.volter.backend.watchItem.dto.WatchItemDetailsResponse;
import com.volter.backend.watchItem.mapper.WatchItemDetailsMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Function;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemMapperTest {

    @Mock
    private GoldItemDetailsMapper goldItemDetailsMapper;

    @Mock
    private ElectronicItemDetailsMapper electronicItemDetailsMapper;

    @Mock
    private VehicleItemDetailsMapper vehicleItemDetailsMapper;

    @Mock
    private WatchItemDetailsMapper watchItemDetailsMapper;

    @Mock
    private OtherItemDetailsMapper otherItemDetailsMapper;

    @Spy
    @InjectMocks
    private ItemMapper itemMapper = new ItemMapperImpl();

    @ParameterizedTest(name = "{0} item should call {0}Mapper and no others")
    @MethodSource("provideItemTypeTestCases")
    @DisplayName("Item type mapping should call correct mapper")
    void testItemTypeMapping(
            ItemType itemType,
            Object mockMapper,
            Function<ItemDetailsResponse, ?> detailsGetter,
            Object expectedDTO
    ) {
        // Arrange
        Item item = createItem(itemType);
        setupMockMapper(mockMapper, expectedDTO);

        // Act
        ItemDetailsResponse itemDetailsResponse = itemMapper.toDetailsResponse(item);

        // Assert
        verifyCorrectMapperCalled(itemType, mockMapper, item);
        verifyOtherMappersNotCalled(itemType);
        verifyResponseHasOnlyExpectedDetails(itemDetailsResponse, itemType, detailsGetter, expectedDTO);
    }

    private static Stream<Arguments> provideItemTypeTestCases() {
        return Stream.of(
                Arguments.of(
                        ItemType.GOLD,
                        "goldItemDetailsMapper",
                        (Function<ItemDetailsResponse, ?>) ItemDetailsResponse::getGoldItemDetails,
                        new GoldItemDetailsResponse()
                ),
                Arguments.of(
                        ItemType.ELECTRONIC,
                        "electronicItemDetailsMapper",
                        (Function<ItemDetailsResponse, ?>) ItemDetailsResponse::getElectronicItemDetails,
                        new ElectronicItemDetailsResponse()
                ),
                Arguments.of(
                        ItemType.VEHICLE,
                        "vehicleItemDetailsMapper",
                        (Function<ItemDetailsResponse, ?>) ItemDetailsResponse::getVehicleItemDetails,
                        new VehicleItemDetailsResponse()
                ),
                Arguments.of(
                        ItemType.WATCH,
                        "watchItemDetailsMapper",
                        (Function<ItemDetailsResponse, ?>) ItemDetailsResponse::getWatchItemDetails,
                        new WatchItemDetailsResponse()
                ),
                Arguments.of(
                        ItemType.OTHER,
                        "otherItemDetailsMapper",
                        (Function<ItemDetailsResponse, ?>) ItemDetailsResponse::getOtherItemDetails,
                        new OtherItemDetailsResponse()
                )
        );
    }

    private void setupMockMapper(Object mapperName, Object dto) {
        switch (mapperName.toString()) {
            case "goldItemDetailsMapper":
                when(goldItemDetailsMapper.toDTO(any())).thenReturn((GoldItemDetailsResponse) dto);
                break;
            case "electronicItemDetailsMapper":
                when(electronicItemDetailsMapper.toDTO(any())).thenReturn((ElectronicItemDetailsResponse) dto);
                break;
            case "vehicleItemDetailsMapper":
                when(vehicleItemDetailsMapper.toDTO(any())).thenReturn((VehicleItemDetailsResponse) dto);
                break;
            case "watchItemDetailsMapper":
                when(watchItemDetailsMapper.toDTO(any())).thenReturn((WatchItemDetailsResponse) dto);
                break;
            case "otherItemDetailsMapper":
                when(otherItemDetailsMapper.toDTO(any())).thenReturn((OtherItemDetailsResponse) dto);
                break;
        }
    }

    private void verifyCorrectMapperCalled(ItemType itemType, Object mapperName, Item item) {
        switch (itemType) {
            case GOLD:
                verify(goldItemDetailsMapper, times(1)).toDTO(item.getGoldItemDetails());
                break;
            case ELECTRONIC:
                verify(electronicItemDetailsMapper, times(1)).toDTO(item.getElectronicItemDetails());
                break;
            case VEHICLE:
                verify(vehicleItemDetailsMapper, times(1)).toDTO(item.getVehicleItemDetails());
                break;
            case WATCH:
                verify(watchItemDetailsMapper, times(1)).toDTO(item.getWatchItemDetails());
                break;
            case OTHER:
                verify(otherItemDetailsMapper, times(1)).toDTO(item.getOtherItemDetails());
                break;
        }
    }

    private void verifyOtherMappersNotCalled(ItemType itemType) {
        switch (itemType) {
            case GOLD:
                verifyNoInteractions(electronicItemDetailsMapper, vehicleItemDetailsMapper, watchItemDetailsMapper, otherItemDetailsMapper);
                break;
            case ELECTRONIC:
                verifyNoInteractions(goldItemDetailsMapper, vehicleItemDetailsMapper, watchItemDetailsMapper, otherItemDetailsMapper);
                break;
            case VEHICLE:
                verifyNoInteractions(goldItemDetailsMapper, electronicItemDetailsMapper, watchItemDetailsMapper, otherItemDetailsMapper);
                break;
            case WATCH:
                verifyNoInteractions(goldItemDetailsMapper, electronicItemDetailsMapper, vehicleItemDetailsMapper, otherItemDetailsMapper);
                break;
            case OTHER:
                verifyNoInteractions(goldItemDetailsMapper, electronicItemDetailsMapper, vehicleItemDetailsMapper, watchItemDetailsMapper);
                break;
        }
    }

    private void verifyResponseHasOnlyExpectedDetails(
            ItemDetailsResponse response,
            ItemType itemType,
            Function<ItemDetailsResponse, ?> detailsGetter,
            Object expectedDTO
    ) {
        assertThat(detailsGetter.apply(response)).isSameAs(expectedDTO);

        // Verify all other details are null
        if (itemType != ItemType.GOLD) {
            assertThat(response.getGoldItemDetails()).isNull();
        }
        if (itemType != ItemType.ELECTRONIC) {
            assertThat(response.getElectronicItemDetails()).isNull();
        }
        if (itemType != ItemType.VEHICLE) {
            assertThat(response.getVehicleItemDetails()).isNull();
        }
        if (itemType != ItemType.WATCH) {
            assertThat(response.getWatchItemDetails()).isNull();
        }
        if (itemType != ItemType.OTHER) {
            assertThat(response.getOtherItemDetails()).isNull();
        }
    }

    private Item createItem(ItemType itemType) {
        return Item.builder()
                .id(1L)
                .itemType(itemType)
                .build();
    }
}