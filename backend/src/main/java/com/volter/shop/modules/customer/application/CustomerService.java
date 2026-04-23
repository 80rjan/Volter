package com.volter.shop.modules.customer.application;

import com.volter.shop.modules.customer.domain.repository.CustomerSpecification;
import com.volter.shop.modules.customer.domain.repository.CustomerRepository;
import com.volter.shop.modules.customer.application.dto.CustomerFilterDTO;
import com.volter.shop.shared.common.exceptions.ResourceNotFoundException;
import com.volter.shop.modules.customer.domain.model.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public Customer getById(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
    }

    @Transactional
    public Customer getReferenceById(Long customerId) {
        return customerRepository.getReferenceById(customerId);
    }

    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }

    public Page<Customer> getAll(CustomerFilterDTO filters, Pageable pageable) {
        Specification<Customer> spec = CustomerSpecification.withFilters(filters);
        return customerRepository.findAll(spec, pageable);
    }

    public void getDetails(Long clientId) {
        return;
    }

    public Customer updateTelephone(Long id, String newPhoneNumber, String newReservePhoneNumber) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        customer.setPhoneNumber(newPhoneNumber);
        customer.setReservePhoneNumber(newReservePhoneNumber);

        return customerRepository.save(customer);
    }
}
