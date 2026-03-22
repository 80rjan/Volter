package com.volter.backend.transaction.domain.repository;

import com.volter.backend.transaction.domain.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long>,
        JpaSpecificationExecutor<Transaction> {

    @Query("""
        SELECT t FROM Transaction t
        WHERE (TYPE(t) = PawnTransaction AND TREAT(t AS PawnTransaction).pawn.customer.id = :customerId)
           OR (TYPE(t) = SaleTransaction AND TREAT(t AS SaleTransaction).sale.customer.id = :customerId)
    """)
    List<Transaction> findByCustomerId(@Param("customerId") Long customerId);
}
