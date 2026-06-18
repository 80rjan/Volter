package com.volter.identity.modules.auth.web;

import com.volter.identity.modules.auth.application.AuthService;
import com.volter.identity.modules.auth.application.dto.LoginRequest;
import com.volter.identity.modules.auth.application.dto.LoginResponse;
import com.volter.identity.modules.auth.application.dto.SelectShopRequest;
import com.volter.identity.modules.auth.application.dto.TokenResponse;
import com.volter.shared.security.StaffPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
}
