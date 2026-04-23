package com.volter.shop.modules.pawn.domain.repository;

import com.volter.shop.modules.pawn.domain.model.Pawn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PawnRepository extends JpaRepository<Pawn, Long>,
        JpaSpecificationExecutor<Pawn> {

    List<Pawn> findByCustomer_Id(Long customerId);

    @Query("SELECT p FROM Pawn p WHERE p.period.maturityDate <= :date")
    List<Pawn> findByPeriod_MaturityDateBefore(@Param("date") LocalDate date);
}
