package com.volter.shop.modules.pawn.domain.repository;

import com.volter.shop.modules.pawn.domain.model.PawnContractEvent;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PawnContractEventRepository extends JpaRepository<PawnContractEvent, Long> {

    // open-in-view is off and the activity list needs each event's contract (for
    // the customer name), so pull it in with the event rather than lazily.
    @EntityGraph(attributePaths = {"pawnContract", "pawnContract.customer"})
    List<PawnContractEvent> findByIdIn(Iterable<Long> ids);

    @EntityGraph(attributePaths = {"pawnContract", "pawnContract.customer"})
    Optional<PawnContractEvent> findWithContractById(Long id);

    /** [eventId, customer full name] for the given event ids — the activity list's client column. */
    @Query("""
            select e.id, e.pawnContract.customer.fullName
            from PawnContractEvent e
            where e.id in :eventIds
            """)
    List<Object[]> findCustomerNamesByEventIds(@Param("eventIds") Collection<Long> eventIds);

    /** Event ids whose contract customer's name matches (case-insensitive, contains). */
    @Query("""
            select e.id
            from PawnContractEvent e
            where upper(e.pawnContract.customer.fullName) like upper(concat('%', :name, '%'))
            """)
    List<Long> findEventIdsByCustomerName(@Param("name") String name);
}
