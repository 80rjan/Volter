package com.volter.identity.modules.auth.application.dto;

import java.util.List;

public record LoginResponse(String preAuthToken, List<ShopOption> shops) {
}
