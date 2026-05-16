package com.volter.shop.modules.customer.web;

import com.volter.shop.modules.customer.application.CustomerService;
import com.volter.shop.modules.customer.application.dto.CustomerFilterDTO;
import com.volter.shop.modules.customer.infrastructure.mapper.CustomerMapper;
import com.volter.shop.modules.customer.web.response.CustomerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base.path}/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final CustomerMapper customerMapper;

    @GetMapping
    public ResponseEntity<Page<CustomerResponse>> getAll(CustomerFilterDTO filter, Pageable pageable) {
        return ResponseEntity.ok(customerService.getAll(filter, pageable).map(customerMapper::toDTO));
    }
}
