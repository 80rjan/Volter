package com.volter.backend.pawn;

import com.volter.backend.customer.Customer;
import com.volter.backend.customer.enums.CustomerRiskLevel;
import com.volter.backend.goldItem.GoldItemDetails;
import com.volter.backend.goldItem.enums.GoldItemCarats;
import com.volter.backend.item.Item;
import com.volter.backend.item.enums.ItemOriginType;
import com.volter.backend.item.enums.ItemStatus;
import com.volter.backend.item.enums.ItemType;
import com.volter.backend.pawn.dto.PawnDetailsResponse;
import com.volter.backend.pawn.mapper.PawnMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class PawnMapperIntegrationTest {

    @Autowired
    private PawnMapper pawnMapper;

    @Test
    @DisplayName("PawnMapper should map Pawn to PawnDetailsResponse and call all nested mappers")
    void testPawnMapping() {
        // Arrange
        Customer customer = Customer.builder()
                .id(1L)
                .name("John Doe")
                .phoneNumber("1234567890")
                .embg("1234567890123")
                .address("123 Main St")
                .city("Anytown")
                .riskLevel(CustomerRiskLevel.MEDIUM)
                .build();
        GoldItemDetails goldItemDetails = GoldItemDetails.builder()
                .itemId(1L)
                .weightGrams(12.5f)
                .pricePerGram(9000f)
                .carats(GoldItemCarats.CARAT_18)
                .pieceType("Necklace")
                .build();
        Item goldItem = Item.builder()
                .id(1L)
                .itemType(ItemType.GOLD)
                .itemOriginType(ItemOriginType.PAWN)
                .itemStatus(ItemStatus.IN_PAWN)
                .description("Gold item")
                .goldItemDetails(goldItemDetails)
                .build();
        Pawn pawn = Pawn.builder()
                .id(1L)
                .amount(10000)
                .interest(1000)
                .issueDate(LocalDate.now())
                .maturityDate(LocalDate.now().plusDays(30))
                .durationDays(30)
                .customer(customer)
                .item(goldItem)
                .build();

        // Act
        PawnDetailsResponse pawnDetailsResponse = pawnMapper.toDetailsResponse(pawn);

        // Assert
        assertThat(pawnDetailsResponse).isNotNull();
        assertThat(pawnDetailsResponse.getAmount()).isEqualTo(10000);
        assertThat(pawnDetailsResponse.getInterest()).isEqualTo(1000);

        assertThat(pawnDetailsResponse.getItem()).isNotNull();
        assertThat(pawnDetailsResponse.getItem().getItemType()).isEqualTo(ItemType.GOLD);
        assertThat(pawnDetailsResponse.getItem().getDescription()).isEqualTo("Gold item");

        assertThat(pawnDetailsResponse.getItem().getGoldItemDetails()).isNotNull();
        assertThat(pawnDetailsResponse.getItem().getGoldItemDetails().getWeightGrams()).isEqualTo(12.5f);
        assertThat(pawnDetailsResponse.getItem().getGoldItemDetails().getCarats()).isEqualTo(GoldItemCarats.CARAT_18);

    }
}
