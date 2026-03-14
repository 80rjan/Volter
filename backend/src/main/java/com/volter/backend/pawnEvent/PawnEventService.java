package com.volter.backend.pawnEvent;

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
