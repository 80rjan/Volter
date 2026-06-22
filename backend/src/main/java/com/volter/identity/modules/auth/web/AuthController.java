package com.volter.identity.modules.auth.web;

import com.volter.identity.modules.auth.application.AuthService;
import com.volter.identity.modules.auth.application.dto.CurrentUserResponse;
import com.volter.identity.modules.auth.application.dto.LoginRequest;
import com.volter.identity.modules.auth.application.dto.LoginResponse;
import com.volter.identity.modules.auth.application.dto.SelectShopRequest;
import com.volter.identity.modules.auth.application.dto.TokenResponse;
import com.volter.identity.modules.staff.application.dto.PasswordChangeRequest;
import com.volter.shared.security.StaffPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for auth actions.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Authenticates a user and returns a login response containing tokens and user information.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request,
                                               HttpServletRequest http) {
        return ResponseEntity.ok(authService.login(request, http.getRemoteAddr(), http.getHeader("User-Agent")));
    }

    /**
     * Selects a shop for the authenticated staff member and returns a token response.
     */
    @PostMapping("/select-shop")
    public ResponseEntity<TokenResponse> selectShop(@Valid @RequestBody SelectShopRequest request,
                                                    @AuthenticationPrincipal StaffPrincipal principal) {
        return ResponseEntity.ok(authService.selectShop(principal.staffId(), request));
    }

    /**
     * Sets a new password for the caller (forced first-login change). Accepts the
     * pre-auth token; until this succeeds, select-shop is refused, so the account
     * cannot be entered.
     */
    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody PasswordChangeRequest request,
                                               @AuthenticationPrincipal StaffPrincipal principal) {
        authService.changePassword(principal.staffId(), request);
        return ResponseEntity.noContent().build();
    }

    /**
     * Returns the authenticated staff member with the roles and permissions
     * resolved for the active shop. The SPA calls this to drive what it renders.
     */
    @GetMapping("/me")
    public ResponseEntity<CurrentUserResponse> me(@AuthenticationPrincipal StaffPrincipal principal) {
        return ResponseEntity.ok(new CurrentUserResponse(
                principal.staffId(),
                principal.username(),
                principal.shopId(),
                principal.roles(),
                principal.permissions()));
    }
}
