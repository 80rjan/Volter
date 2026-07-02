package com.volter.identity.staff;

import com.volter.identity.modules.permission.domain.model.Permission;
import com.volter.identity.modules.role.domain.model.Role;
import com.volter.identity.modules.staff.domain.model.Staff;
import com.volter.identity.modules.staff.domain.model.enums.StaffStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pure domain-logic tests for {@link Staff}: compensation maths, lifecycle/status,
 * the self-management guard and the per-shop IAM checks.
 */
class StaffTest {

    private Staff staff(Long id, int baseSalary, BigDecimal profitSharePercent) {
        return Staff.builder()
                .id(id)
                .fullName("Jane Doe")
                .username("jane")
                .passwordHash("x")
                .nationalId("NID-" + id)
                .phonePrimary("000")
                .baseSalary(baseSalary)
                .profitSharePercent(profitSharePercent)
                .build();
    }

    @Nested
    @DisplayName("lifecycle")
    class Lifecycle {

        @Test
        @DisplayName("a fresh ACTIVE, non-deleted staff member is active")
        void freshStaffIsActive() {
            assertTrue(staff(1L, 1000, BigDecimal.ZERO).isActive());
        }

        @Test
        @DisplayName("soft delete marks deleted, flips to INACTIVE and is no longer active")
        void softDeleteDeactivates() {
            Staff s = staff(1L, 1000, BigDecimal.ZERO);

            s.softDelete();

            assertTrue(s.isDeleted());
            assertFalse(s.isActive());
            assertEquals(StaffStatus.INACTIVE, s.getStatus());
        }

        @Test
        @DisplayName("soft delete is idempotent: a second call keeps the original timestamp")
        void softDeleteIsIdempotent() {
            Staff s = staff(1L, 1000, BigDecimal.ZERO);

            s.softDelete();
            OffsetDateTime firstDeletedAt = s.getDeletedAt();
            s.softDelete();

            assertSame(firstDeletedAt, s.getDeletedAt());
        }
    }

    @Nested
    @DisplayName("assignManager")
    class AssignManager {

        @Test
        @DisplayName("a staff member cannot be their own manager")
        void rejectsSelfManagement() {
            Staff s = staff(1L, 1000, BigDecimal.ZERO);
            Staff self = staff(1L, 1000, BigDecimal.ZERO); // same id

            assertThrows(IllegalArgumentException.class, () -> s.assignManager(self));
        }

        @Test
        @DisplayName("a different manager is accepted")
        void acceptsAnotherManager() {
            Staff s = staff(1L, 1000, BigDecimal.ZERO);
            Staff boss = staff(2L, 2000, BigDecimal.ZERO);

            s.assignManager(boss);

            assertSame(boss, s.getManager());
        }
    }

    @Nested
    @DisplayName("per-shop IAM")
    class Iam {

        private static final Long SHOP = 7L;
        private static final Long OTHER_SHOP = 8L;

        private Role adminRole() {
            Permission read = Permission.builder().name("PAWN_READ").build();
            return Role.builder().name("ADMIN").permissions(Set.of(read)).build();
        }

        @Test
        @DisplayName("a granted role is reported only for the shop it was granted in")
        void roleIsScopedToShop() {
            Staff s = staff(1L, 1000, BigDecimal.ZERO);
            s.assignRole(adminRole(), SHOP);

            assertTrue(s.hasRole("ADMIN", SHOP));
            assertFalse(s.hasRole("ADMIN", OTHER_SHOP));
            assertFalse(s.hasRole("MANAGER", SHOP));
        }

        @Test
        @DisplayName("a role's permissions are visible via hasPermission for its shop")
        void permissionFlowsThroughRole() {
            Staff s = staff(1L, 1000, BigDecimal.ZERO);
            s.assignRole(adminRole(), SHOP);

            assertTrue(s.hasPermission("PAWN_READ", SHOP));
            assertFalse(s.hasPermission("PAWN_READ", OTHER_SHOP));
            assertFalse(s.hasPermission("PAWN_WRITE", SHOP));
        }

        @Test
        @DisplayName("worksInShop reflects a granted assignment")
        void worksInShop() {
            Staff s = staff(1L, 1000, BigDecimal.ZERO);
            s.assignRole(adminRole(), SHOP);

            assertTrue(s.worksInShop(SHOP));
            assertFalse(s.worksInShop(OTHER_SHOP));
        }

        @Test
        @DisplayName("granting the same role twice in one shop is rejected")
        void rejectsDuplicateGrant() {
            Staff s = staff(1L, 1000, BigDecimal.ZERO);
            Role role = adminRole();
            s.assignRole(role, SHOP);

            assertThrows(IllegalStateException.class, () -> s.assignRole(role, SHOP));
        }

        @Test
        @DisplayName("a revoked role no longer counts for hasRole or worksInShop")
        void revokedRoleIsInert() {
            Staff s = staff(1L, 1000, BigDecimal.ZERO);
            Role role = adminRole();
            s.assignRole(role, SHOP);

            s.revokeRole(role, SHOP);

            assertFalse(s.hasRole("ADMIN", SHOP));
            assertFalse(s.worksInShop(SHOP));
        }
    }
}
