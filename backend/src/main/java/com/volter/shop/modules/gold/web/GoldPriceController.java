package com.volter.shop.modules.gold.web;

import com.volter.shop.modules.gold.application.GoldPriceService;
import com.volter.shop.modules.gold.web.response.GoldPriceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base.path}/gold")
public class GoldPriceController {

    private final GoldPriceService goldPriceService;

    @GetMapping("/price")
    public ResponseEntity<GoldPriceResponse> getLivePrice() {
        return ResponseEntity.ok(goldPriceService.getLivePrice());
    }
}
