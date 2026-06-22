package com.volter.identity.modules.staff.web;

import com.volter.identity.modules.staff.application.StaffService;
import com.volter.identity.modules.staff.application.dto.StaffOptionResponse;
import com.volter.shared.security.StaffPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Lightweight staff lookups available to any authenticated staff member (no admin
 * permission), used to drive the per-page "filter by staff" dropdowns.
 */
@RestController
@RequestMapping("/staff")
@RequiredArgsConstructor
public class StaffDirectoryController {

    private final StaffService staffService;

    /**
     * The caller's team: themselves plus everyone below them in the management tree.
     */
    @GetMapping("/team")
    public ResponseEntity<List<StaffOptionResponse>> team(@AuthenticationPrincipal StaffPrincipal principal) {
        return ResponseEntity.ok(staffService.listTeam(principal.staffId()));
    }
}
