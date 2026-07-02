package com.volter.shop.modules.transaction.domain.repository;

import com.volter.shop.modules.transaction.domain.model.Transaction;
import com.volter.shop.modules.transaction.domain.model.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;

public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

    /** Total amount of a given transaction type made by one staff member from {@code since} onward. */
    @Query("""
            select coalesce(sum(t.amount.amount), 0L)
            from Transaction t
            where t.staffId = :staffId
              and t.type = :type
              and t.createdAt >= :since
            """)
    long sumAmountByStaffAndTypeSince(@Param("staffId") Long staffId,
                                      @Param("type") TransactionType type,
                                      @Param("since") OffsetDateTime since);
}
