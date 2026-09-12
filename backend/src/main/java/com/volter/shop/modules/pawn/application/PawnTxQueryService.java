package com.volter.shop.modules.pawn.application;

import com.volter.identity.modules.staff.application.StaffService;
import com.volter.shop.modules.pawn.application.dto.PawnContractDetailedResponse;
import com.volter.shop.modules.pawn.application.dto.PawnContractEventResponse;
import com.volter.shop.modules.pawn.domain.repository.PawnContractRepository;
import com.volter.shop.modules.pawn.domain.repository.PawnContractEventRepository;
import com.volter.shop.modules.pawn.domain.repository.PawnContractNoteRepository;
import com.volter.shop.modules.pawn.domain.repository.PawnTransactionRepository;
import com.volter.shop.modules.pawn.infrastructure.mapper.PawnContractMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Read-only pawn lookup by transaction id, consumed by the transaction module
 * when assembling a transaction's detailed view. Kept separate from
 * {@link PawnService} (which depends on the transaction module) so this never
 * forms a service dependency cycle: it touches only pawn repositories/mappers.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PawnTxQueryService {

    private final PawnTransactionRepository pawnTxRepository;
    private final PawnContractRepository contractRepository;
    private final PawnContractNoteRepository noteRepository;
    private final PawnContractEventRepository eventRepository;
    private final PawnContractMapper pawnContractMapper;
    private final StaffService staffService;

    /** The detailed pawn contract behind a PAWN transaction, if any. */
    public Optional<PawnContractDetailedResponse> findDetailByTransactionId(Long transactionId) {
        return pawnTxRepository.findPawnContractIdByTransactionId(transactionId)
                .flatMap(contractRepository::findById)
                .map(contract -> pawnContractMapper.toDetailedResponse(
                        contract,
                        staffService.findStaffNames(Set.of(contract.getCreatedByStaffId()))
                                .get(contract.getCreatedByStaffId()),
                        noteRepository.findByPawnContractIdOrderByCreatedAtDesc(contract.getId())));
    }

    /** Maps each of the given transaction ids that is a PAWN transaction to its customer's name. */
    public Map<Long, String> findCustomerNamesByTransactionIds(Collection<Long> transactionIds) {
        if (transactionIds.isEmpty()) return Map.of();
        Map<Long, String> names = new HashMap<>();
        for (Object[] row : pawnTxRepository.findCustomerNamesByTransactionIds(transactionIds)) {
            names.put((Long) row[0], (String) row[1]);
        }
        return names;
    }

    /** Ids of PAWN transactions whose contract customer's name matches the query. */
    public Set<Long> findTransactionIdsByCustomerName(String name) {
        return new HashSet<>(pawnTxRepository.findTransactionIdsByCustomerName(name));
    }

    /** A single non-monetary pawn contract event, for its detailed view. */
    public Optional<PawnContractEventResponse> findEventById(Long eventId) {
        return eventRepository.findWithContractById(eventId)
                .map(e -> new PawnContractEventResponse(
                        e.getId(), e.getPawnContract().getId(), e.getAction(),
                        e.getPerformedByStaffId(), e.getDescription(), e.getOccurredAt()));
    }

    /** The detailed pawn contract behind a non-monetary pawn contract event, if any. */
    public Optional<PawnContractDetailedResponse> findDetailByEventId(Long eventId) {
        return eventRepository.findWithContractById(eventId)
                .map(event -> event.getPawnContract().getId())
                .flatMap(contractRepository::findById)
                .map(contract -> pawnContractMapper.toDetailedResponse(
                        contract,
                        staffService.findStaffNames(Set.of(contract.getCreatedByStaffId()))
                                .get(contract.getCreatedByStaffId()),
                        noteRepository.findByPawnContractIdOrderByCreatedAtDesc(contract.getId())));
    }

    /** Maps each of the given pawn event ids to its contract's customer name. */
    public Map<Long, String> findCustomerNamesByEventIds(Collection<Long> eventIds) {
        if (eventIds.isEmpty()) return Map.of();
        Map<Long, String> names = new HashMap<>();
        for (Object[] row : eventRepository.findCustomerNamesByEventIds(eventIds)) {
            names.put((Long) row[0], (String) row[1]);
        }
        return names;
    }

    /** Pawn event ids whose contract customer's name matches the query. */
    public Set<Long> findEventIdsByCustomerName(String name) {
        return new HashSet<>(eventRepository.findEventIdsByCustomerName(name));
    }

    /** Pawn provision a staff member has generated since the given moment (bonus base). */
    public long provisionForStaffSince(Long staffId, OffsetDateTime since) {
        return pawnTxRepository.provisionForStaffSince(staffId, since);
    }

    /** Shop-wide pawn provision (interest income) collected between {@code from} and {@code to} (inclusive). */
    public long provisionBetween(LocalDate from, LocalDate to) {
        return pawnTxRepository.provisionBetween(from, to);
    }
}
