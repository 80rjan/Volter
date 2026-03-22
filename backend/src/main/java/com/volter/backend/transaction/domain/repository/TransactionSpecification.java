package com.volter.backend.transaction.domain.repository;

import com.volter.backend.transaction.domain.model.Transaction;
import com.volter.backend.transaction.application.dto.TransactionFilterDTO;
import com.volter.backend.transaction.domain.model.enums.TransactionCategory;
import com.volter.backend.cashRegister.domain.model.CashRegisterTransaction;
import com.volter.backend.expense.domain.model.ExpenseTransaction;
import com.volter.backend.pawn.domain.model.PawnTransaction;
import com.volter.backend.sale.domain.model.SaleTransaction;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class TransactionSpecification {

    private static Class<? extends Transaction> getClassForCategory(TransactionCategory category) {
        return switch (category) {
            case PAWN -> PawnTransaction.class;
            case SALE -> SaleTransaction.class;
            case EXPENSE -> ExpenseTransaction.class;
            case CASH_REGISTER -> CashRegisterTransaction.class;
        };
    }

    public static Specification<Transaction> withFilters(TransactionFilterDTO filters) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter by transaction category (discriminator)
            if (filters.getTransactionCategory() != null) {
                predicates.add(cb.equal(
                        root.type(),
                        getClassForCategory(filters.getTransactionCategory())
                ));
            }

            // Filter by customer name (in Pawn OR Sale)
            if (filters.getCustomerName() != null && !filters.getCustomerName().isBlank()) {
                Predicate pawnCustomer = cb.and(
                        cb.equal(root.type(), PawnTransaction.class),
                        cb.like(
                                cb.lower(cb.treat(root, PawnTransaction.class).get("pawn").get("customer").get("name")),
                                "%" + filters.getCustomerName().toLowerCase() + "%"
                        )
                );

                Predicate saleCustomer = cb.and(
                        cb.equal(root.type(), SaleTransaction.class),
                        cb.like(
                                cb.lower(cb.treat(root, SaleTransaction.class).get("sale").get("customer").get("name")),
                                "%" + filters.getCustomerName().toLowerCase() + "%"
                        )
                );

                predicates.add(cb.or(pawnCustomer, saleCustomer));
            }

            // Filter by customer EMBG (in Pawn OR Sale)
            if (filters.getCustomerEmbg() != null && !filters.getCustomerEmbg().isBlank()) {
                Predicate pawnEmbg = cb.and(
                        cb.equal(root.type(), PawnTransaction.class),
                        cb.equal(
                                cb.treat(root, PawnTransaction.class).get("pawn").get("customer").get("embg"),
                                filters.getCustomerEmbg()
                        )
                );

                Predicate saleEmbg = cb.and(
                        cb.equal(root.type(), SaleTransaction.class),
                        cb.equal(
                                cb.treat(root, SaleTransaction.class).get("sale").get("customer").get("embg"),
                                filters.getCustomerEmbg()
                        )
                );

                predicates.add(cb.or(pawnEmbg, saleEmbg));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
