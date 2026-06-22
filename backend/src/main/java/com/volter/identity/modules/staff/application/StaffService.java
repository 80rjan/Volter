package com.volter.identity.modules.staff.application;

import com.volter.identity.modules.role.domain.model.Role;
import com.volter.identity.modules.role.domain.repository.RoleRepository;
import com.volter.identity.modules.staff.application.dto.PasswordChangeRequest;
import com.volter.identity.modules.staff.application.dto.StaffCreateRequest;
import com.volter.identity.modules.staff.application.dto.StaffFilterRequest;
import com.volter.identity.modules.staff.application.dto.StaffOptionResponse;
import com.volter.identity.modules.staff.application.dto.StaffShopAssignmentResponse;
import com.volter.identity.modules.staff.application.dto.StaffUpdateRequest;
import com.volter.identity.modules.staff.domain.model.Staff;
import com.volter.identity.modules.staff.domain.model.StaffRole;
import com.volter.identity.modules.staff.domain.repository.StaffRepository;
import com.volter.identity.modules.staff.domain.repository.StaffRoleRepository;
import com.volter.identity.modules.staff.domain.specification.StaffSpecification;
import com.volter.platform.modules.shop.domain.model.Shop;
import com.volter.platform.modules.shop.domain.model.StaffShop;
import com.volter.platform.modules.shop.domain.model.enums.StaffShopStatus;
import com.volter.platform.modules.shop.domain.repository.ShopRepository;
import com.volter.platform.modules.shop.domain.repository.StaffShopRepository;
import com.volter.shared.web.exception.BusinessRuleException;
import com.volter.shared.web.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StaffService {

    private final StaffRepository staffRepository;
    private final StaffRoleRepository staffRoleRepository;
    private final RoleRepository roleRepository;
    private final ShopRepository shopRepository;
    private final StaffShopRepository staffShopRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * List staff members, filtered and paginated. The caller can see all staff members.
     */
    @Transactional(readOnly = true)
    public Page<Staff> list(StaffFilterRequest filter, Pageable pageable) {
        return staffRepository.findAll(StaffSpecification.matches(filter), pageable);
    }

    /**
     * A single staff member, only if the caller has access to it (is their manager or themselves).
     */
    @Transactional(readOnly = true)
    public Staff get(Long id, Long principalId) {
        if (!staffRepository.isManagerOf(principalId, id) && !principalId.equals(id)) {
            throw new ResourceNotFoundException("Staff not found: " + id);
        }
        return loadStaff(id);
    }

    /**
     * Create a new staff member. The caller must have a username and national ID that are not already in use.
     */
    public Staff create(StaffCreateRequest request) {
        if (staffRepository.existsByUsername(request.username())) {
            throw new BusinessRuleException("Username already exists: " + request.username());
        }
        if (staffRepository.existsByNationalId(request.nationalId())) {
            throw new BusinessRuleException("National id already exists");
        }
        Staff manager = resolveManager(request.managerId());

        Staff staff = Staff.builder()
                .fullName(request.fullName())
                .username(request.username())
                .passwordHash(passwordEncoder.encode(request.password()))
                // The manager-set password is temporary; force a change on first login.
                .passwordChangeRequired(true)
                .nationalId(request.nationalId())
                .phonePrimary(request.phonePrimary())
                .phoneSecondary(request.phoneSecondary())
                .baseSalary(request.baseSalary())
                .bonusPercent(request.bonusPercent())
                .manager(manager)
                .build();
        return staffRepository.save(staff);
    }

    /**
     * Update an existing staff member. The caller must be the manager of the staff member.
     */
    public Staff update(Long id, StaffUpdateRequest request, Long principalId) {
        if (!isManagerOf(principalId, id)) {
            throw new BusinessRuleException("A staff member can be updated only by his manager (direct or indirect)");
        }

        Staff staff = loadStaff(id);
        staff.updateProfile(request.phonePrimary(), request.phoneSecondary(),
                request.baseSalary(), request.bonusPercent());
        staff.assignManager(resolveManager(request.managerId()));
        return staff;
    }

    /**
     * Activate a staff member. The caller must be the manager of the staff member.
     */
    public void activate(Long id, Long principalId) {
        if (!isManagerOf(principalId, id)) {
            throw new BusinessRuleException("A staff member can be activated only by his manager (direct or indirect)");
        }

        loadStaff(id).activate();
    }

    /**
     * Deactivate a staff member. The caller must be the manager of the staff member.
     */
    public void deactivate(Long id, Long principalId) {
        if (!isManagerOf(principalId, id)) {
            throw new BusinessRuleException("A staff member can be deactivated only by his manager (direct or indirect)");
        }

        loadStaff(id).deactivate();
    }

    /**
     * Suspend a staff member. The caller must be the manager of the staff member.
     */
    public void suspend(Long id, Long principalId) {
        if (!isManagerOf(principalId, id)) {
            throw new BusinessRuleException("A staff member can be suspended only by his manager (direct or indirect)");
        }

        loadStaff(id).suspend();
    }

    /**
     * Soft delete a staff member. The caller must be the manager of the staff member.
     */
    public void softDelete(Long id, Long principalId) {
        if (!isManagerOf(principalId, id)) {
            throw new BusinessRuleException("A staff member can be soft deleted only by his manager (direct or indirect)");
        }

        Staff staff = loadStaff(id);
        staff.softDelete();
    }

    // ----- shop + role assignment (IAM) -----

    /**
     * Active shop assignments for a staff member, each with the single role they
     * hold in that shop (role fields null if the role was revoked but the shop kept).
     */
    @Transactional(readOnly = true)
    public List<StaffShopAssignmentResponse> listShopAssignments(Long staffId) {
        Staff staff = loadStaff(staffId);
        return staffShopRepository.findAllByStaffIdAndStatus(staffId, StaffShopStatus.ACTIVE).stream()
                .map(ss -> {
                    Long shopId = ss.getShop().getId();
                    Optional<StaffRole> role = staff.getStaffRoles().stream()
                            .filter(sr -> sr.appliesTo(shopId) && sr.isGranted())
                            .findFirst();
                    return new StaffShopAssignmentResponse(
                            shopId, ss.getShop().getName(), ss.getShop().getCode(),
                            role.map(StaffRole::getId).orElse(null),
                            role.map(sr -> sr.getRole().getId()).orElse(null),
                            role.map(sr -> sr.getRole().getName()).orElse(null));
                })
                .toList();
    }

    /**
     * Assign a staff member to a shop and grant them a role there, as one unit.
     * Enforces one role per shop. Re-uses (re-activates) any previously revoked
     * shop/role rows to respect the unique constraints.
     */
    public void assignShopWithRole(Long staffId, Long shopId, Long roleId) {
        Staff staff = loadStaff(staffId);
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Shop not found: " + shopId));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleId));

        boolean alreadyHasRole = staff.getStaffRoles().stream()
                .anyMatch(sr -> sr.appliesTo(shopId) && sr.isGranted());
        if (alreadyHasRole) {
            throw new BusinessRuleException("Staff already has a role in this shop");
        }

        // Activate (or create) the shop assignment.
        staffShopRepository.findByStaffIdAndShop_Id(staffId, shopId)
                .ifPresentOrElse(StaffShop::reassign,
                        () -> staffShopRepository.save(StaffShop.assign(staffId, shop)));

        // Grant the role: re-activate the (staff, role, shop) row if it exists, else create.
        staff.getStaffRoles().stream()
                .filter(sr -> sr.appliesTo(shopId) && sr.getRole().getId().equals(roleId))
                .findFirst()
                .ifPresentOrElse(StaffRole::regrant,
                        () -> staff.assignRole(role, shopId));
    }

    /**
     * Revoke just the role a staff member holds in a shop (the shop assignment stays).
     */
    public void revokeRole(Long staffId, Long staffRoleId) {
        Staff staff = loadStaff(staffId);
        StaffRole grant = staff.getStaffRoles().stream()
                .filter(sr -> sr.getId().equals(staffRoleId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Staff role grant not found: " + staffRoleId));
        grant.revoke();
    }

    /**
     * Remove a staff member from a shop: deactivate the assignment and revoke any
     * role they held there.
     */
    public void revokeShop(Long staffId, Long shopId) {
        StaffShop assignment = staffShopRepository.findByStaffIdAndShop_Id(staffId, shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Shop assignment not found for shop: " + shopId));
        assignment.unassign();

        Staff staff = loadStaff(staffId);
        staff.getStaffRoles().stream()
                .filter(sr -> sr.appliesTo(shopId) && sr.isGranted())
                .forEach(StaffRole::revoke);
    }


    // ----- CROSS MODULE HELPERS -----

    /**
     * Ids of all staff below {@code managerId} in the management tree (recursive,
     * excluding the manager). Other modules use this to scope "my team" queries.
     */
    @Transactional(readOnly = true)
    public List<Long> findSubordinateStaffIds(Long managerId) {
        return staffRepository.findSubordinateIds(managerId);
    }

    /**
     * The caller's own team for pickers: themselves plus everyone below them in the
     * management tree, as {id, fullName}. Used by the per-page "filter by staff" dropdown.
     */
    @Transactional(readOnly = true)
    public List<StaffOptionResponse> listTeam(Long staffId) {
        List<Long> ids = new ArrayList<>(staffRepository.findSubordinateIds(staffId));
        ids.add(staffId);
        return staffRepository.findAllById(ids).stream()
                .map(s -> new StaffOptionResponse(s.getId(), s.getFullName()))
                .sorted(java.util.Comparator.comparing(StaffOptionResponse::fullName))
                .toList();
    }

    /**
     * Full names of the given staff members, keyed by id. Used by other modules
     * to enrich responses that reference staff only by id.
     */
    @Transactional(readOnly = true)
    public Map<Long, String> findStaffNames(Collection<Long> ids) {
        if (ids.isEmpty()) {
            return Map.of();
        }
        return staffRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(Staff::getId, Staff::getFullName));
    }

    /**
     * Whether {@code managerId} is a direct or indirect manager of {@code staffId}.
     */
    @Transactional(readOnly = true)
    public boolean isManagerOf(Long managerId, Long staffId) {
        return staffRepository.isManagerOf(managerId, staffId);
    }

    /**
     * Id of the staff member's direct manager, if any. Used to route risk flags.
     */
    @Transactional(readOnly = true)
    public Optional<Long> findManagerId(Long staffId) {
        return staffRepository.findManagerId(staffId);
    }


    // ----- INTERNAL HELPERS -----

    private Staff loadStaff(Long id) {
        return staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found: " + id));
    }

    private Staff resolveManager(Long managerId) {
        if (managerId == null) return null;
        return staffRepository.findById(managerId)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found: " + managerId));
    }
}
