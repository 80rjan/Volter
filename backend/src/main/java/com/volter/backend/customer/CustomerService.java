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

    public CustomerRiskLevel calculateRiskLevel(Customer customer) {
        if (customer.getTotalPawnCount() == 0) return CustomerRiskLevel.LOW;

        double lateRate = (double) customer.getLateRenewalCount() / customer.getTotalPawnCount();
        double forfeitRate = (double) customer.getForfeitCount() / customer.getTotalPawnCount();

        // HIGH risk conditions
        if (forfeitRate > 0.6) return CustomerRiskLevel.HIGH;
        if (lateRate > 0.6 && customer.getAvgDaysLate() > 10) return CustomerRiskLevel.HIGH;

        // MEDIUM risk conditions
        if (forfeitRate > 0.2) return CustomerRiskLevel.MEDIUM;
        if (lateRate > 0.3 || customer.getAvgDaysLate() > 5) return CustomerRiskLevel.MEDIUM;

        return CustomerRiskLevel.LOW;
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
