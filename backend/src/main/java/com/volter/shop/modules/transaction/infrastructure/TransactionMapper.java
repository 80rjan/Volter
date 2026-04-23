package com.volter.shop.modules.transaction.infrastructure;

import com.volter.shop.modules.cashregister.domain.model.CashRegisterTransaction;
import com.volter.shop.modules.cashregister.infrastructure.CashRegisterMapper;
import com.volter.shop.modules.expense.domain.model.ExpenseTransaction;
import com.volter.shop.modules.expense.infrastructure.ExpenseMapper;
import com.volter.shop.modules.pawn.domain.model.PawnTransaction;
import com.volter.shop.modules.pawn.infrastructure.PawnMapper;
import com.volter.shop.modules.sale.domain.model.SaleTransaction;
import com.volter.shop.modules.sale.infrastructure.SaleMapper;
import com.volter.shop.modules.transaction.application.dto.response.TransactionDetailedResponse;
import com.volter.shop.modules.transaction.application.dto.response.TransactionResponse;
import com.volter.shop.modules.transaction.application.dto.response.categories.TransactionDetailedCashRegisterResponse;
import com.volter.shop.modules.transaction.application.dto.response.categories.TransactionDetailedExpenseResponse;
import com.volter.shop.modules.transaction.application.dto.response.categories.TransactionDetailedPawnResponse;
import com.volter.shop.modules.transaction.application.dto.response.categories.TransactionDetailedSaleResponse;
import com.volter.shop.modules.transaction.domain.model.Transaction;
import com.volter.shop.shared.valueobject.Money;
import org.mapstruct.Mapper;
import org.mapstruct.SubclassExhaustiveStrategy;
import org.mapstruct.SubclassMapping;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {PawnMapper.class, SaleMapper.class, ExpenseMapper.class, CashRegisterMapper.class},
        subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION   // for exception
)
public abstract class TransactionMapper {

    public abstract TransactionResponse toResponse(Transaction transaction);

    public abstract List<TransactionResponse> toResponse(List<Transaction> transactions);

    @SubclassMapping(source = PawnTransaction.class, target = TransactionDetailedPawnResponse.class)
    @SubclassMapping(source = SaleTransaction.class, target = TransactionDetailedSaleResponse.class)
    @SubclassMapping(source = ExpenseTransaction.class, target = TransactionDetailedExpenseResponse.class)
    @SubclassMapping(source = CashRegisterTransaction.class, target = TransactionDetailedCashRegisterResponse.class)
    public abstract TransactionDetailedResponse toDetailedResponse(Transaction transaction);

    public abstract TransactionDetailedPawnResponse toDetailedPawnResponse(PawnTransaction transaction);

    public abstract TransactionDetailedSaleResponse toDetailedSaleResponse(SaleTransaction transaction);

    public abstract TransactionDetailedExpenseResponse toDetailedExpenseResponse(ExpenseTransaction transaction);

    public abstract TransactionDetailedCashRegisterResponse toDetailedCashRegisterResponse(CashRegisterTransaction transaction);

    protected Integer map(Money money) {
        return money != null ? money.amount() : null;
    }
}
