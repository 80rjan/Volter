package com.volter.platform.modules.authevent.web;

import com.volter.platform.modules.authevent.application.AuthEventService;
import com.volter.platform.modules.authevent.application.dto.AuthEventFilterRequest;
import com.volter.platform.modules.authevent.application.dto.AuthEventResponse;
import com.volter.platform.modules.authevent.infrastructure.mapper.AuthEventMapper;
import com.volter.shared.security.StaffPrincipal;
import com.volter.shared.web.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing authentication events.
 */
@RestController
@RequestMapping("/admin/auth-events")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('AUDIT_READ')")
public class AuthEventController {

    private final AuthEventService authEventService;
    private final AuthEventMapper authEventMapper;

    /**
     * List auth events of the caller and of staff members below them (recursively), with optional filters.
     */
    @GetMapping
    public ResponseEntity<PageResponse<AuthEventResponse>> list(@ModelAttribute AuthEventFilterRequest filter,
                                                                Pageable pageable,
                                                                @AuthenticationPrincipal StaffPrincipal principal) {
        return ResponseEntity.ok(PageResponse.of(
                authEventService.list(filter, pageable, principal.staffId()), authEventMapper::toResponse));
    }
}
