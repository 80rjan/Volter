package com.volter.shared.security;

import com.volter.identity.modules.permission.domain.model.Permission;
import com.volter.identity.modules.staff.domain.model.Staff;
import com.volter.identity.modules.staff.domain.model.enums.StaffRoleStatus;
import com.volter.identity.modules.staff.domain.repository.StaffRepository;
import com.volter.platform.modules.shop.domain.model.Shop;
import com.volter.platform.modules.shop.domain.repository.ShopRepository;
import com.volter.shared.web.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StaffPrincipalService {

    private final StaffRepository staffRepository;
    private final ShopRepository shopRepository;

    /**
     * Loads a pre-auth StaffPrincipal containing only staff ID and username, used for initial authentication before shop selection.
     * Validates that the staff member is active.
     * No shop information, roles, or permissions are included at this stage.
     */
    @Transactional(readOnly = true)
    public StaffPrincipal loadPreAuth(Long staffId) {
        Staff staff = staffRepository.findById(staffId)
                .filter(Staff::isActive)
                .orElseThrow(() -> new UnauthorizedException("Staff not found or inactive"));
        return StaffPrincipal.preAuth(staff.getId(), staff.getUsername());
    }

    /**
     * Loads a fully populated StaffPrincipal for the given staff ID and shop ID, including roles and permissions.
     * Validates that the staff member is active, the shop is active, and that the staff member is assigned to the shop.
     */
    @Transactional(readOnly = true)
    public StaffPrincipal loadAccess(Long staffId, Long shopId) {
        Staff staff = staffRepository.findById(staffId)
                .filter(Staff::isActive)
                .orElseThrow(() -> new UnauthorizedException("Staff not found or inactive"));

        Shop shop = shopRepository.findById(shopId)
                .filter(Shop::isActive)
                .orElseThrow(() -> new UnauthorizedException("Shop not found or inactive"));

        if (!staff.worksInShop(shopId)) {
            throw new UnauthorizedException("Staff is not assigned to the active shop");
        }

        Set<String> roles = staff.getStaffRoles().stream()
                .filter(sr -> sr.getStatus() == StaffRoleStatus.GRANTED && sr.appliesTo(shopId))
                .map(sr -> sr.getRole().getName())
                .collect(Collectors.toSet());

        Set<String> permissions = staff.getStaffRoles().stream()
                .filter(sr -> sr.getStatus() == StaffRoleStatus.GRANTED && sr.appliesTo(shopId))
                .flatMap(sr -> sr.getRole().getPermissions().stream())
                .map(Permission::getName)
                .collect(Collectors.toSet());

        return StaffPrincipal.access(
                staff.getId(),
                staff.getUsername(),
                shop.getId(),
                shop.getSchemaName(),
                roles,
                permissions
        );
    }
}
