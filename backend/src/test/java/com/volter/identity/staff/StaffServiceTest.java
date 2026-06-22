package com.volter.identity.staff;

import com.volter.identity.modules.role.domain.repository.RoleRepository;
import com.volter.identity.modules.staff.application.StaffService;
import com.volter.identity.modules.staff.application.dto.StaffCreateRequest;
import com.volter.identity.modules.staff.application.dto.StaffUpdateRequest;
import com.volter.identity.modules.staff.domain.model.Staff;
import com.volter.identity.modules.staff.domain.model.StaffRole;
import com.volter.identity.modules.staff.domain.repository.StaffRepository;
import com.volter.identity.modules.staff.domain.repository.StaffRoleRepository;
import com.volter.platform.modules.shop.domain.repository.ShopRepository;
import com.volter.shared.web.exception.BusinessRuleException;
import com.volter.shared.web.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link StaffService}: the create-time uniqueness checks and the
 * "only a (direct or indirect) manager may act" authorization guard that wraps
 * every mutation and the single-record read.
 */
@ExtendWith(MockitoExtension.class)
class StaffServiceTest {

    @Mock private StaffRepository staffRepository;
    @Mock private StaffRoleRepository staffRoleRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private ShopRepository shopRepository;
    @Mock private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @InjectMocks private StaffService service;

    private static final Long PRINCIPAL = 3L;
    private static final Long TARGET = 5L;

    private StaffCreateRequest createRequest(Long managerId) {
        return new StaffCreateRequest("Jane Doe", "jane", "password123", "NID-1",
                "000", null, 1000, new BigDecimal("10.00"), managerId);
    }

    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        @DisplayName("rejects a duplicate username")
        void duplicateUsername_throws() {
            when(staffRepository.existsByUsername("jane")).thenReturn(true);

            assertThrows(BusinessRuleException.class, () -> service.create(createRequest(null)));
            verify(staffRepository, never()).save(any());
        }

        @Test
        @DisplayName("rejects a duplicate national id")
        void duplicateNationalId_throws() {
            when(staffRepository.existsByUsername("jane")).thenReturn(false);
            when(staffRepository.existsByNationalId("NID-1")).thenReturn(true);

            assertThrows(BusinessRuleException.class, () -> service.create(createRequest(null)));
            verify(staffRepository, never()).save(any());
        }

        @Test
        @DisplayName("rejects a manager id that does not resolve")
        void missingManager_throws() {
            when(staffRepository.existsByUsername("jane")).thenReturn(false);
            when(staffRepository.existsByNationalId("NID-1")).thenReturn(false);
            when(staffRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> service.create(createRequest(99L)));
            verify(staffRepository, never()).save(any());
        }

        @Test
        @DisplayName("stores the bcrypt-encoded password, not the raw one")
        void encodesPassword() {
            when(staffRepository.existsByUsername("jane")).thenReturn(false);
            when(staffRepository.existsByNationalId("NID-1")).thenReturn(false);
            when(passwordEncoder.encode("password123")).thenReturn("hashed");
            when(staffRepository.save(any(Staff.class))).thenAnswer(inv -> inv.getArgument(0));

            Staff created = service.create(createRequest(null));

            assertEquals("hashed", created.getPasswordHash());
            assertNull(created.getManager());
        }
    }

    @Nested
    @DisplayName("get()")
    class Get {

        @Test
        @DisplayName("returns the caller's own record")
        void ownRecord_ok() {
            Staff self = Staff.builder().id(PRINCIPAL).build();
            when(staffRepository.isManagerOf(PRINCIPAL, PRINCIPAL)).thenReturn(false);
            when(staffRepository.findById(PRINCIPAL)).thenReturn(Optional.of(self));

            assertEquals(self, service.get(PRINCIPAL, PRINCIPAL));
        }

        @Test
        @DisplayName("lets a manager read a subordinate")
        void managerReadsSubordinate_ok() {
            Staff target = Staff.builder().id(TARGET).build();
            when(staffRepository.isManagerOf(PRINCIPAL, TARGET)).thenReturn(true);
            when(staffRepository.findById(TARGET)).thenReturn(Optional.of(target));

            assertEquals(target, service.get(TARGET, PRINCIPAL));
        }

        @Test
        @DisplayName("hides an unrelated staff member behind 'not found'")
        void unrelated_throwsNotFound() {
            when(staffRepository.isManagerOf(PRINCIPAL, TARGET)).thenReturn(false);

            assertThrows(ResourceNotFoundException.class, () -> service.get(TARGET, PRINCIPAL));
            verify(staffRepository, never()).findById(TARGET);
        }
    }

    @Nested
    @DisplayName("manager-guarded mutations")
    class GuardedMutations {

        @Test
        @DisplayName("update is refused for a non-manager and never loads the target")
        void update_nonManager_throws() {
            when(staffRepository.isManagerOf(PRINCIPAL, TARGET)).thenReturn(false);

            assertThrows(BusinessRuleException.class,
                    () -> service.update(TARGET, updateRequest(), PRINCIPAL));
            verify(staffRepository, never()).findById(any());
        }

        @Test
        @DisplayName("update applies profile changes and (re)assigns the manager")
        void update_manager_appliesChanges() {
            Staff target = org.mockito.Mockito.mock(Staff.class);
            when(staffRepository.isManagerOf(PRINCIPAL, TARGET)).thenReturn(true);
            when(staffRepository.findById(TARGET)).thenReturn(Optional.of(target));

            service.update(TARGET, updateRequest(), PRINCIPAL);

            verify(target).updateProfile("111", null, 2000, new BigDecimal("5.00"));
            verify(target).assignManager(null); // null managerId -> detached from manager
        }

        @Test
        @DisplayName("activate is refused for a non-manager")
        void activate_nonManager_throws() {
            when(staffRepository.isManagerOf(PRINCIPAL, TARGET)).thenReturn(false);

            assertThrows(BusinessRuleException.class, () -> service.activate(TARGET, PRINCIPAL));
        }

        @Test
        @DisplayName("activate flips the target ACTIVE for a manager")
        void activate_manager_ok() {
            Staff target = org.mockito.Mockito.mock(Staff.class);
            when(staffRepository.isManagerOf(PRINCIPAL, TARGET)).thenReturn(true);
            when(staffRepository.findById(TARGET)).thenReturn(Optional.of(target));

            service.activate(TARGET, PRINCIPAL);

            verify(target).activate();
        }

        @Test
        @DisplayName("deactivate flips the target INACTIVE for a manager")
        void deactivate_manager_ok() {
            Staff target = org.mockito.Mockito.mock(Staff.class);
            when(staffRepository.isManagerOf(PRINCIPAL, TARGET)).thenReturn(true);
            when(staffRepository.findById(TARGET)).thenReturn(Optional.of(target));

            service.deactivate(TARGET, PRINCIPAL);

            verify(target).deactivate();
        }

        @Test
        @DisplayName("suspend suspends the target for a manager")
        void suspend_manager_ok() {
            Staff target = org.mockito.Mockito.mock(Staff.class);
            when(staffRepository.isManagerOf(PRINCIPAL, TARGET)).thenReturn(true);
            when(staffRepository.findById(TARGET)).thenReturn(Optional.of(target));

            service.suspend(TARGET, PRINCIPAL);

            verify(target).suspend();
        }

        @Test
        @DisplayName("soft delete is refused for a non-manager")
        void softDelete_nonManager_throws() {
            when(staffRepository.isManagerOf(PRINCIPAL, TARGET)).thenReturn(false);

            assertThrows(BusinessRuleException.class, () -> service.softDelete(TARGET, PRINCIPAL));
        }

        @Test
        @DisplayName("soft delete soft-deletes the target for a manager")
        void softDelete_manager_ok() {
            Staff target = org.mockito.Mockito.mock(Staff.class);
            when(staffRepository.isManagerOf(PRINCIPAL, TARGET)).thenReturn(true);
            when(staffRepository.findById(TARGET)).thenReturn(Optional.of(target));

            service.softDelete(TARGET, PRINCIPAL);

            verify(target).softDelete();
        }

        private StaffUpdateRequest updateRequest() {
            return new StaffUpdateRequest("111", null, 2000, new BigDecimal("5.00"), null);
        }
    }

    @Nested
    @DisplayName("revokeRole()")
    class RevokeRole {

        @Test
        @DisplayName("throws when the grant does not belong to the staff member")
        void grantNotFound_throws() {
            Staff target = org.mockito.Mockito.mock(Staff.class);
            StaffRole other = org.mockito.Mockito.mock(StaffRole.class);
            when(staffRepository.findById(TARGET)).thenReturn(Optional.of(target));
            when(target.getStaffRoles()).thenReturn(List.of(other));
            when(other.getId()).thenReturn(99L);

            assertThrows(ResourceNotFoundException.class,
                    () -> service.revokeRole(TARGET, 10L));
        }

        @Test
        @DisplayName("revokes the matching grant")
        void revokesMatchingGrant() {
            Staff target = org.mockito.Mockito.mock(Staff.class);
            StaffRole grant = org.mockito.Mockito.mock(StaffRole.class);
            when(staffRepository.findById(TARGET)).thenReturn(Optional.of(target));
            when(target.getStaffRoles()).thenReturn(List.of(grant));
            when(grant.getId()).thenReturn(10L);

            service.revokeRole(TARGET, 10L);

            verify(grant).revoke();
        }
    }

    @Nested
    @DisplayName("cross-module helpers")
    class Helpers {

        @Test
        @DisplayName("findStaffNames short-circuits on an empty id set without hitting the repo")
        void findStaffNames_empty() {
            assertTrue(service.findStaffNames(List.of()).isEmpty());
            verify(staffRepository, never()).findAllById(any());
        }

        @Test
        @DisplayName("findStaffNames maps id -> full name")
        void findStaffNames_maps() {
            Staff a = Staff.builder().id(1L).fullName("Alice").build();
            Staff b = Staff.builder().id(2L).fullName("Bob").build();
            when(staffRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(a, b));

            Map<Long, String> names = service.findStaffNames(List.of(1L, 2L));

            assertEquals(Map.of(1L, "Alice", 2L, "Bob"), names);
        }

        @Test
        @DisplayName("findSubordinateStaffIds delegates to the recursive repository query")
        void findSubordinateStaffIds_delegates() {
            when(staffRepository.findSubordinateIds(PRINCIPAL)).thenReturn(List.of(5L, 6L));

            assertEquals(List.of(5L, 6L), service.findSubordinateStaffIds(PRINCIPAL));
        }

        @Test
        @DisplayName("isManagerOf delegates to the recursive repository query")
        void isManagerOf_delegates() {
            when(staffRepository.isManagerOf(PRINCIPAL, TARGET)).thenReturn(true);

            assertTrue(service.isManagerOf(PRINCIPAL, TARGET));
            verifyNoInteractions(roleRepository, shopRepository, staffRoleRepository);
        }
    }
}
