package com.volter.shop.expense;

import com.volter.shop.modules.expense.web.response.ExpenseResponse;
import com.volter.shop.modules.expense.domain.model.Expense;
import com.volter.shop.modules.expense.domain.model.enums.ExpenseType;
import com.volter.shop.modules.expense.infrastructure.mapper.ExpenseMapper;
import com.volter.shop.modules.expense.infrastructure.mapper.ExpenseMapperImpl;
import com.volter.shop.shared.valueobject.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ExpenseMapperImpl.class)
class ExpenseMapperTest {

    @Autowired
    private ExpenseMapper expenseMapper;

    // ─── fixture ─────────────────────────────────────────────────────────────

    private Expense expense() {
        return Expense.builder()
                .expenseType(ExpenseType.RENT)
                .amount(new Money(1500))
                .description("Monthly rent")
                .date(LocalDate.of(2025, 5, 1))
                .build();
    }

    // =========================================================================
    // toResponse() — field mapping
    // =========================================================================

    @Nested
    @DisplayName("toResponse() — field mapping")
    class FieldMapping {

        @Test
        @DisplayName("maps expenseType")
        void mapsExpenseType() {
            assertThat(expenseMapper.toResponse(expense()).expenseType()).isEqualTo(ExpenseType.RENT);
        }

        @Test
        @DisplayName("maps amount from Money to Integer")
        void mapsAmount_fromMoneyToInteger() {
            assertThat(expenseMapper.toResponse(expense()).amount()).isEqualTo(1500);
        }

        @Test
        @DisplayName("maps description")
        void mapsDescription() {
            assertThat(expenseMapper.toResponse(expense()).description()).isEqualTo("Monthly rent");
        }

        @Test
        @DisplayName("maps date")
        void mapsDate() {
            assertThat(expenseMapper.toResponse(expense()).date()).isEqualTo(LocalDate.of(2025, 5, 1));
        }

        @Test
        @DisplayName("createdAt and updatedAt are null when @PrePersist has not fired")
        void timestamps_areNullWithoutPrePersist() {
            ExpenseResponse r = expenseMapper.toResponse(expense());
            assertThat(r.createdAt()).isNull();
            assertThat(r.updatedAt()).isNull();
        }
    }

    // =========================================================================
    // toResponse() — Money null-safety
    // =========================================================================

    @Nested
    @DisplayName("toResponse() — null Money handling")
    class NullMoney {

        @Test
        @DisplayName("null amount maps to null Integer")
        void nullAmount_mapsToNull() {
            Expense e = Expense.builder()
                    .expenseType(ExpenseType.UTILITIES)
                    .amount(null)
                    .description("test")
                    .date(LocalDate.now())
                    .build();
            assertThat(expenseMapper.toResponse(e).amount()).isNull();
        }
    }

    // =========================================================================
    // toResponse() — all ExpenseTypes are mapped
    // =========================================================================

    @Nested
    @DisplayName("toResponse() — all expense types")
    class AllExpenseTypes {

        @Test
        @DisplayName("UTILITIES type is preserved")
        void utilitiesType_isMapped() {
            Expense e = Expense.builder()
                    .expenseType(ExpenseType.UTILITIES)
                    .amount(new Money(200))
                    .description("Electric bill")
                    .date(LocalDate.now())
                    .build();
            assertThat(expenseMapper.toResponse(e).expenseType()).isEqualTo(ExpenseType.UTILITIES);
        }

        @Test
        @DisplayName("SALARIES type is preserved")
        void salariesType_isMapped() {
            Expense e = Expense.builder()
                    .expenseType(ExpenseType.SALARIES)
                    .amount(new Money(30000))
                    .description("Staff salary")
                    .date(LocalDate.now())
                    .build();
            assertThat(expenseMapper.toResponse(e).expenseType()).isEqualTo(ExpenseType.SALARIES);
        }
    }
}
