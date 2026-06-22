package com.volter.platform.modules.gold.web;

import com.volter.platform.modules.gold.application.GoldPriceService;
import com.volter.platform.modules.gold.application.dto.GoldPriceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes the live gold price (proxied from goldapi.io) to the SPA.
 */
@RestController
@RequestMapping("/gold")
@RequiredArgsConstructor
public class GoldPriceController {

    private final GoldPriceService goldPriceService;

    @GetMapping("/price")
    public ResponseEntity<GoldPriceResponse> price() {
        return ResponseEntity.ok(goldPriceService.current());
    }
}
