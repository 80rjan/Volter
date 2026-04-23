package com.volter.shop.modules.pawn.domain.repository;

import com.volter.shop.modules.pawn.domain.model.event.PawnEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PawnEventRepository extends JpaRepository<PawnEvent, Long> {
}
