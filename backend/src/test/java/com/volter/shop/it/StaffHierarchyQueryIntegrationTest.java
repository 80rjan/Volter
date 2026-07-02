package com.volter.shop.it;

import com.volter.identity.modules.staff.domain.model.Staff;
import com.volter.identity.modules.staff.domain.repository.StaffRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Exercises the native, recursive {@code WITH RECURSIVE} CTEs on {@link StaffRepository}
 * against a real PostgreSQL. These power the management-tree access scoping used across
 * the app (cash-register sessions, discrepancies, transactions), so their runtime
 * behaviour — not just bootstrap validation — must be proven.
 *
 * <p>Hierarchy built per test (arrows point manager -> report):
 * <pre>
 *   ceo
 *    +-- managerA
 *    |     +-- employeeA1
 *    |     +-- employeeA2
 *    +-- managerB
 *          +-- employeeB1   (leaf)
 * </pre>
 *
 * All rows are created with unique usernames/national-ids and removed in teardown
 * (leaves first, to respect the self-referencing {@code manager_id} FK), so the
 * persistent external database stays clean and assertions are deterministic.
 */
class StaffHierarchyQueryIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private StaffRepository staffRepository;

    /** Tracks saved staff in creation order so teardown can delete reports before managers. */
    private final List<Staff> created = new ArrayList<>();

    private Staff ceo;
    private Staff managerA;
    private Staff managerB;
    private Staff employeeA1;
    private Staff employeeA2;
    private Staff employeeB1;

    @BeforeEach
    void buildHierarchy() {
        ceo = save(staff("ceo", null));
        managerA = save(staff("managerA", ceo));
        managerB = save(staff("managerB", ceo));
        employeeA1 = save(staff("employeeA1", managerA));
        employeeA2 = save(staff("employeeA2", managerA));
        employeeB1 = save(staff("employeeB1", managerB));
    }

    @AfterEach
    void tearDown() {
        // Delete in reverse creation order: reports (created later) go before their managers.
        for (int i = created.size() - 1; i >= 0; i--) {
            staffRepository.delete(created.get(i));
        }
        staffRepository.flush();
        created.clear();
    }

    // ----- findSubordinateIds -----

    @Test
    void findSubordinateIds_returnsEveryDescendantAcrossLevels() {
        assertThat(staffRepository.findSubordinateIds(ceo.getId()))
                .containsExactlyInAnyOrder(
                        managerA.getId(), managerB.getId(),
                        employeeA1.getId(), employeeA2.getId(), employeeB1.getId());
    }

    @Test
    void findSubordinateIds_returnsOnlyTheManagersOwnSubtree() {
        assertThat(staffRepository.findSubordinateIds(managerA.getId()))
                .containsExactlyInAnyOrder(employeeA1.getId(), employeeA2.getId());
    }

    @Test
    void findSubordinateIds_isEmptyForALeaf() {
        assertThat(staffRepository.findSubordinateIds(employeeB1.getId())).isEmpty();
    }

    // ----- isManagerOf -----

    @Test
    void isManagerOf_isTrueForADirectReport() {
        assertThat(staffRepository.isManagerOf(managerA.getId(), employeeA1.getId())).isTrue();
    }

    @Test
    void isManagerOf_isTrueForAnIndirectReportSeveralLevelsDown() {
        assertThat(staffRepository.isManagerOf(ceo.getId(), employeeA1.getId())).isTrue();
    }

    @Test
    void isManagerOf_isFalseAcrossSiblingBranches() {
        assertThat(staffRepository.isManagerOf(managerA.getId(), employeeB1.getId())).isFalse();
    }

    @Test
    void isManagerOf_isFalseWhenAskedUpsideDown() {
        // A report is not a manager of their own manager.
        assertThat(staffRepository.isManagerOf(employeeA1.getId(), managerA.getId())).isFalse();
    }

    @Test
    void isManagerOf_isFalseForSelf() {
        assertThat(staffRepository.isManagerOf(ceo.getId(), ceo.getId())).isFalse();
    }

    // ----- helpers -----

    private Staff save(Staff staff) {
        Staff persisted = staffRepository.saveAndFlush(staff);
        created.add(persisted);
        return persisted;
    }

    private Staff staff(String role, Staff manager) {
        String unique = role + "_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return Staff.builder()
                .fullName(role)
                .username(unique)
                .passwordHash("x")
                .nationalId(unique)
                .phonePrimary("000")
                .baseSalary(1000)
                .profitSharePercent(BigDecimal.ZERO)
                .manager(manager)
                .build();
    }
}
