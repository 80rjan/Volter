package com.volter.identity.modules.auth.application.dto;

import java.util.Set;

/**
 * The authenticated staff member for the active shop, including the roles and
 * permissions resolved for that shop. The SPA uses this to decide what to render.
 */
public record CurrentUserResponse(
        Long staffId,
        String username,
        Long shopId,
        Set<String> roles,
        Set<String> permissions
) {
}
