package com.volter.shop.modules.sale.api;

import com.volter.shop.modules.sale.application.SaleService;
import com.volter.shop.modules.sale.application.dto.filter.SaleFilter;
import com.volter.shop.modules.sale.application.dto.request.SaleCreationRequest;
import com.volter.shop.modules.sale.application.dto.request.SaleSellRequest;
import com.volter.shop.modules.sale.application.dto.response.SaleDetailedResponse;
import com.volter.shop.modules.sale.application.dto.response.SaleResponse;
import com.volter.shop.modules.sale.domain.model.Sale;
import com.volter.shop.modules.sale.infrastructure.SaleMapper;
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
    public ResponseEntity<Page<SaleResponse>> getAll(SaleFilter filter, Pageable pageable) {
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
