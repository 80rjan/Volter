package com.volter.identity.modules.permission.web;

import com.volter.identity.modules.permission.application.PermissionService;
import com.volter.identity.modules.permission.application.dto.PermissionFilterRequest;
import com.volter.identity.modules.permission.application.dto.PermissionResponse;
import com.volter.identity.modules.permission.infrastructure.mapper.PermissionMapper;
import com.volter.shared.web.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing permissions.
 * Access is restricted to users with the 'IAM_MANAGE' authority, which are usually administrators.
 */
@RestController
@RequestMapping("/admin/permissions")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('IAM_MANAGE')")
public class PermissionController {

    private final PermissionService permissionService;
    private final PermissionMapper permissionMapper;

    /**
     * Lists permissions based on the provided filter and pagination parameters.
     */
    @GetMapping
    public ResponseEntity<PageResponse<PermissionResponse>> list(@ModelAttribute PermissionFilterRequest filter,
                                                                 Pageable pageable) {
        return ResponseEntity.ok(PageResponse.of(permissionService.list(filter, pageable), permissionMapper::toResponse));
    }

    /**
     * Retrieves a specific permission by its ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PermissionResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(permissionMapper.toResponse(permissionService.get(id)));
    }
}
