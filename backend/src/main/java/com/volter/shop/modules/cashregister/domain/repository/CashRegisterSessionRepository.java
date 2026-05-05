package com.volter.shop.modules.cashregister.domain.repository;

import com.volter.shop.modules.cashregister.domain.model.CashRegisterSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CashRegisterSessionRepository extends JpaRepository<CashRegisterSession, Long> {

    @Query("select s from CashRegisterSession s where s.staff.id = :staffId and s.status = 'OPEN'")
    CashRegisterSession findOpenSessionByStaffId(@Param("staffId") Long staffId);

    @Query("""
                select case when count(s) > 0 then true else false end
                from CashRegisterSession s
                where s.staff.id = :staffId
                and s.status = 'OPEN'
            """)
    boolean existsByStaffIdAndStatusOpen(@Param("staffId") Long staffId);
}
