package com.volter.shop.modules.customer.web;

import com.volter.shop.modules.customer.application.CustomerService;
import com.volter.shop.modules.customer.application.dto.CustomerCreateRequest;
import com.volter.shop.modules.customer.application.dto.CustomerFilterRequest;
import com.volter.shop.modules.customer.application.dto.CustomerResponse;
import com.volter.shop.modules.customer.application.dto.CustomerUpdateRequest;
import com.volter.shop.modules.customer.infrastructure.mapper.CustomerMapper;
import com.volter.shared.web.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for customer management.
 */
@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final CustomerMapper customerMapper;

    /**
     * List customers with optional filtering and pagination.
     */
    @GetMapping
    @PreAuthorize("hasAuthority('CUSTOMER_READ')")
    public ResponseEntity<PageResponse<CustomerResponse>> list(@ModelAttribute CustomerFilterRequest filter, Pageable pageable) {
        return ResponseEntity.ok(PageResponse.of(customerService.list(filter, pageable), customerMapper::toResponse));
    }

    /**
     * Get a single customer by ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CUSTOMER_READ')")
    public ResponseEntity<CustomerResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(customerMapper.toResponse(customerService.get(id)));
    }

    /**
     * Create a new customer.
     */
    @PostMapping
    @PreAuthorize("hasAuthority('CUSTOMER_WRITE')")
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerMapper.toResponse(customerService.create(request)));
    }

    /**
     * Update an existing customer, fetched by its ID.
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('CUSTOMER_WRITE')")
    public ResponseEntity<CustomerResponse> update(@PathVariable Long id, @Valid @RequestBody CustomerUpdateRequest request) {
        return ResponseEntity.ok(customerMapper.toResponse(customerService.update(id, request)));
    }
}
