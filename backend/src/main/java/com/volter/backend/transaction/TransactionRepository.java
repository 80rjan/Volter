package com.volter.backend.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("""
        select  t from Transaction t
        where t.pawn.customer.id = :customerId
        or t.sale.customer.id = :customerId
    """)
    List<Transaction> findByCustomerId(@Param("customerId") Long customerId);
}
