package com.volter.backend.pawn.application;

import com.volter.backend.pawn.domain.model.event.PawnEvent;
import com.volter.backend.pawn.domain.repository.PawnEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PawnEventService {

    private final PawnEventRepository pawnEventRepository;

    public PawnEvent save(PawnEvent pawnEvent) {
        return pawnEventRepository.save(pawnEvent);
    }
}
