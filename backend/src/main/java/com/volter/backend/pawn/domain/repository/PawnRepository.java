package com.volter.backend.pawn.domain.repository;

import com.volter.backend.pawn.domain.model.Pawn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PawnRepository extends JpaRepository<Pawn, Long> {


    List<Pawn> findByCustomer_Id(Long customerId);
}
