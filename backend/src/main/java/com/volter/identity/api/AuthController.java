package com.volter.identity.api;

import com.volter.identity.application.AuthService;
import com.volter.identity.application.dto.LoginRequest;
import com.volter.identity.application.dto.SwitchShopRequest;
import com.volter.identity.application.dto.TokenDTO;
import com.volter.identity.domain.model.IdentityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.base.path}/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/switch-shop")
    public ResponseEntity<TokenDTO> switchShop(@RequestBody SwitchShopRequest request,
                                               @AuthenticationPrincipal IdentityUser user) {
        return ResponseEntity.ok(authService.switchShop(request, user));
    }
}
