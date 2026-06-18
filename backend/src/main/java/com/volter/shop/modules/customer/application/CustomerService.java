package com.volter.shop.modules.customer.application;

import com.volter.shop.modules.customer.application.dto.CustomerCreateRequest;
import com.volter.shop.modules.customer.application.dto.CustomerFilterRequest;
import com.volter.shop.modules.customer.application.dto.CustomerUpdateRequest;
import com.volter.shop.modules.customer.domain.model.Customer;
import com.volter.shop.modules.customer.domain.repository.CustomerRepository;
import com.volter.shop.modules.customer.domain.specification.CustomerSpecification;
import com.volter.shared.web.exception.BusinessRuleException;
import com.volter.shared.web.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;

    /**
     * List customers with optional filtering and pagination.
     */
    @Transactional(readOnly = true)
    public Page<Customer> list(CustomerFilterRequest filter, Pageable pageable) {
        return customerRepository.findAll(CustomerSpecification.matches(filter), pageable);
    }

    /**
     * Get a single customer by ID.
     */
    @Transactional(readOnly = true)
    public Customer get(Long id) {
        return loadCustomer(id);
    }

    /**
     * Create a new customer, ensuring the national ID is unique.
     */
    public Customer create(CustomerCreateRequest request) {
        if (customerRepository.existsByNationalId(request.nationalId())) {
            throw new BusinessRuleException("Customer with this national id already exists");
        }
        Customer customer = Customer.builder()
                .fullName(request.fullName())
                .nationalId(request.nationalId())
                .phonePrimary(request.phonePrimary())
                .phoneSecondary(request.phoneSecondary())
                .address(request.address())
                .city(request.city())
                .build();
        return customerRepository.save(customer);
    }

    /**
     * Update an existing customer, fetched by its ID.
     */
    public Customer update(Long id, CustomerUpdateRequest request) {
        Customer customer = loadCustomer(id);
        customer.rename(request.fullName());
        customer.updateContactDetails(request.phonePrimary(), request.phoneSecondary(), request.address(), request.city());
        return customer;
    }

    //HELPERS

    /**
     * Load a customer by ID or fail.
     */
    public Customer findOrFail(Long id) {
        return loadCustomer(id);
    }

    /**
     * Load a customer by ID or throw a ResourceNotFoundException if not found.
     */
    private Customer loadCustomer(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + id));
    }
}
