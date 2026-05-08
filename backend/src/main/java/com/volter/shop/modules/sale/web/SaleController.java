package com.volter.shop.modules.sale.web;

import com.volter.shop.modules.sale.application.SaleService;
import com.volter.shop.modules.sale.web.request.SaleFilterRequest;
import com.volter.shop.modules.sale.web.request.SaleCreationRequest;
import com.volter.shop.modules.sale.web.request.SaleSellRequest;
import com.volter.shop.modules.sale.web.response.SaleDetailedResponse;
import com.volter.shop.modules.sale.web.response.SaleResponse;
import com.volter.shop.modules.sale.domain.model.Sale;
import com.volter.shop.modules.sale.infrastructure.mapper.SaleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base.path}/sales")
public class SaleController {

    private final SaleService saleService;
    private final SaleMapper saleMapper;

    @GetMapping
    public ResponseEntity<Page<SaleResponse>> getAll(SaleFilterRequest filter, Pageable pageable) {
        Page<Sale> sales = saleService.getAll(filter, pageable);
        return ResponseEntity.ok(sales.map(saleMapper::toResponse));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleDetailedResponse> getById(Long id) {
        return ResponseEntity.ok(saleMapper.toDetailedResponse(saleService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<SaleResponse> create(@RequestBody SaleCreationRequest request) {
        Sale sale = saleService.create(request);
        return ResponseEntity.ok(saleMapper.toResponse(sale));
    }

    @PostMapping("/{id}/sell")
    public ResponseEntity<SaleResponse> sell(@PathVariable Long id, @RequestBody SaleSellRequest request) {
        Sale sale = saleService.sell(id, request);
        return ResponseEntity.ok(saleMapper.toResponse(sale));
    }
}
