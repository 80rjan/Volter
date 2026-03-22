package com.volter.backend.pawn.domain.repository;

import com.volter.backend.pawn.domain.model.event.PawnEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PawnEventRepository extends JpaRepository<PawnEvent, Long> {
}
