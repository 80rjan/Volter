package com.volter.identity.modules.staff.domain.repository;

import com.volter.identity.modules.staff.domain.model.Staff;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StaffRepository extends JpaRepository<Staff, Long>, JpaSpecificationExecutor<Staff> {

    Optional<Staff> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByNationalId(String nationalId);

    /**
     * Ids of every staff member below {@code managerId} in the management tree
     * (direct reports and, recursively, their reports). Excludes the manager.
     */
    @Query(value = """
            WITH RECURSIVE subordinates AS (
                SELECT id FROM public.staff WHERE manager_id = :managerId
                UNION ALL
                SELECT s.id FROM public.staff s
                    JOIN subordinates sub ON s.manager_id = sub.id
            )
            SELECT id FROM subordinates
            """, nativeQuery = true)
    List<Long> findSubordinateIds(@Param("managerId") Long managerId);

    /**
     * True if {@code managerId} sits anywhere above {@code staffId} in the
     * management tree (a direct or indirect manager). Climbs the manager chain
     * up from the staff member; false when they are the same person.
     */
    @Query(value = """
            WITH RECURSIVE ancestors(id) AS (
                SELECT manager_id FROM public.staff WHERE id = :staffId
                UNION ALL
                SELECT s.manager_id FROM public.staff s
                    JOIN ancestors a ON s.id = a.id
            )
            SELECT EXISTS (SELECT 1 FROM ancestors WHERE id = :managerId)
            """, nativeQuery = true)
    boolean isManagerOf(@Param("managerId") Long managerId, @Param("staffId") Long staffId);

    // open-in-view is disabled; the detailed response (mapped in the controller)
    // walks the staffRoles collection, so fetch it eagerly here. The role behind
    // each grant is already EAGER on StaffRole, so it comes along.
    @Override
    @EntityGraph(attributePaths = "staffRoles")
    Optional<Staff> findById(Long id);
}
