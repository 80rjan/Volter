package com.volter.shop.modules.pawn.domain.repository;

import com.volter.shop.modules.pawn.domain.model.PawnContractNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PawnContractNoteRepository extends JpaRepository<PawnContractNote, Long> {

    /**
     * Notes of one contract, newest first. Kept off the PawnContract entity graph
     * on purpose: `extensions` is already fetched there, and fetching two List
     * associations in one query throws MultipleBagFetchException.
     */
    List<PawnContractNote> findByPawnContractIdOrderByCreatedAtDesc(Long pawnContractId);
}
