package com.volter.shop.modules.pawn.domain.repository;

import com.volter.shop.modules.pawn.domain.model.PawnNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PawnNoteRepository extends JpaRepository<PawnNote, Long> {

    /**
     * Notes of one contract, newest first. Kept off the PawnContract entity graph
     * on purpose: `extensions` is already fetched there, and fetching two List
     * associations in one query throws MultipleBagFetchException.
     */
    List<PawnNote> findByPawnContractIdOrderByCreatedAtDesc(Long pawnContractId);
}
