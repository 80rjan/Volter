package com.volter.backend.customer;

import com.volter.backend.customer.enums.CustomerRiskLevel;
import com.volter.backend.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }

    public List<Customer> getAll() {
        return customerRepository.findAll();
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
