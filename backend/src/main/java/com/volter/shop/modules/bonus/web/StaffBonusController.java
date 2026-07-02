package com.volter.shop.modules.bonus.web;

import com.volter.shop.modules.bonus.application.StaffBonusService;
import com.volter.shop.modules.bonus.application.dto.StaffBonusAvailableResponse;
import com.volter.shop.modules.bonus.application.dto.StaffBonusWithdrawRequest;
import com.volter.shared.security.StaffPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Profit-share bonus: every staff member may view and withdraw their own bonus;
 * a manager may view the available bonus of staff below them.
 */
@RestController
@RequestMapping("/staff-bonus")
@RequiredArgsConstructor
public class StaffBonusController {

    private final StaffBonusService staffBonusService;

    /** The caller's own available bonus in their active shop (for the withdraw modal). */
    @GetMapping("/available")
    public ResponseEntity<StaffBonusAvailableResponse> myAvailable(@AuthenticationPrincipal StaffPrincipal principal) {
        return ResponseEntity.ok(staffBonusService.available(principal.staffId(), principal.shopId(), principal.staffId()));
    }

    /** A specific staff member's available bonus (self or a subordinate); authorization is enforced in the service. */
    @GetMapping("/available/{staffId}")
    public ResponseEntity<StaffBonusAvailableResponse> available(@PathVariable Long staffId,
                                                                 @AuthenticationPrincipal StaffPrincipal principal) {
        return ResponseEntity.ok(staffBonusService.available(staffId, principal.shopId(), principal.staffId()));
    }

    /** Withdraw part or all of the caller's available bonus from an open session. */
    @PostMapping
    public ResponseEntity<Void> withdraw(@Valid @RequestBody StaffBonusWithdrawRequest request,
                                         @AuthenticationPrincipal StaffPrincipal principal) {
        staffBonusService.withdraw(principal.staffId(), principal.shopId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
