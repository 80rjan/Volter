package com.volter.identity.modules.role.web;

import com.volter.identity.modules.role.application.RoleService;
import com.volter.identity.modules.role.application.dto.RoleCreateRequest;
import com.volter.identity.modules.role.application.dto.RoleDetailedResponse;
import com.volter.identity.modules.role.application.dto.RoleFilterRequest;
import com.volter.identity.modules.role.application.dto.RolePermissionGrantRequest;
import com.volter.identity.modules.role.application.dto.RoleResponse;
import com.volter.identity.modules.role.application.dto.RoleUpdateRequest;
import com.volter.identity.modules.role.infrastructure.mapper.RoleMapper;
import com.volter.shared.web.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing roles.
 * Only accessible to users with the IAM_MANAGE authority, which is typically reserved for administrators.
 */
@RestController
@RequestMapping("/admin/roles")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('IAM_MANAGE')")
public class RoleController {

    private final RoleService roleService;
    private final RoleMapper roleMapper;

    /**
     * List all roles, filtered by the optional criteria.
     */
    @GetMapping
    public ResponseEntity<PageResponse<RoleResponse>> list(@ModelAttribute RoleFilterRequest filter,
                                                           Pageable pageable) {
        return ResponseEntity.ok(PageResponse.of(roleService.list(filter, pageable), roleMapper::toResponse));
    }

    /**
     * Get a single role by its id.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RoleDetailedResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(roleMapper.toDetailedResponse(roleService.get(id)));
    }

    /**
     * Create a new role.
     */
    @PostMapping
    public ResponseEntity<RoleDetailedResponse> create(@Valid @RequestBody RoleCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roleMapper.toDetailedResponse(roleService.create(request)));
    }

    /**
     * Rename an existing role.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<RoleDetailedResponse> rename(@PathVariable Long id,
                                                       @Valid @RequestBody RoleUpdateRequest request) {
        return ResponseEntity.ok(roleMapper.toDetailedResponse(roleService.rename(id, request)));
    }

    // future: implement soft delete if needed

    /**
     * Grant one or more permissions to a role. Permissions that are already granted will be ignored.
     */
    @PostMapping("/{id}/permissions")
    public ResponseEntity<RoleDetailedResponse> grantPermissions(@PathVariable Long id,
                                                                 @Valid @RequestBody RolePermissionGrantRequest request) {
        return ResponseEntity.ok(roleMapper.toDetailedResponse(roleService.grantPermissions(id, request)));
    }

    /**
     * Revoke a permission from a role. If the role doesn't have the permission, this will be a no-op.
     */
    @DeleteMapping("/{id}/permissions/{permissionId}")
    public ResponseEntity<RoleDetailedResponse> revokePermission(@PathVariable Long id,
                                                                 @PathVariable Long permissionId) {
        return ResponseEntity.ok(roleMapper.toDetailedResponse(roleService.revokePermission(id, permissionId)));
    }
}
