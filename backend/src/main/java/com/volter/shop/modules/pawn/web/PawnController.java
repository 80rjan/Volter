package com.volter.shop.modules.pawn.web;

import com.volter.shop.modules.pawn.application.PawnService;
import com.volter.shop.modules.pawn.web.request.PawnFilterRequest;
import com.volter.shop.modules.pawn.web.request.*;
import com.volter.shop.modules.pawn.web.response.PawnDetailedResponse;
import com.volter.shop.modules.pawn.web.response.PawnResponse;
import com.volter.shop.modules.pawn.domain.model.Pawn;
import com.volter.shop.modules.pawn.infrastructure.mapper.PawnMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base.path}/pawns")
public class PawnController {
    private final PawnService pawnService;
    private final PawnMapper pawnMapper;

    @GetMapping
    public ResponseEntity<Page<PawnResponse>> getAll(PawnFilterRequest filter, Pageable pageable) {
        Page<Pawn> pawns = pawnService.getAll(filter, pageable);
        return ResponseEntity.ok(pawns.map(pawnMapper::toResponse));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PawnDetailedResponse> getById(@PathVariable Long id) {
        Pawn pawn = pawnService.getById(id);
        return ResponseEntity.ok(pawnMapper.toDetailedResponse(pawn));
    }

    @PostMapping
    public ResponseEntity<PawnResponse> create(@RequestBody PawnCreationRequest request) {
        Pawn pawn = pawnService.create(request);
        return ResponseEntity.ok(pawnMapper.toResponse(pawn));
    }

    @PostMapping("/{id}/redeem")
    public ResponseEntity<PawnResponse> redeem(@PathVariable Long id, @RequestBody PawnRedemptionRequest request) {
        Pawn pawn = pawnService.redeem(id, request);
        return ResponseEntity.ok(pawnMapper.toResponse(pawn));
    }

    @PostMapping("/{id}/forfeit")
    public ResponseEntity<PawnResponse> forfeit(@PathVariable Long id, @RequestBody PawnForfeitureRequest request) {
        Pawn pawn = pawnService.forfeit(id, request);
        return ResponseEntity.ok(pawnMapper.toResponse(pawn));
    }

    @PostMapping("/{id}/renew")
    public ResponseEntity<PawnResponse> renew(@PathVariable Long id, @RequestBody PawnRenewalRequest request) {
        Pawn pawn = pawnService.renew(id, request);
        return ResponseEntity.ok(pawnMapper.toResponse(pawn));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PawnResponse> modify(@PathVariable Long id, @RequestBody PawnModificationRequest request) {
        Pawn pawn = pawnService.modify(id, request);
        return ResponseEntity.ok(pawnMapper.toResponse(pawn));
    }
}
