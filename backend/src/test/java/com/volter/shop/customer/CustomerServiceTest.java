package com.volter.shop.customer;

import com.volter.shop.modules.customer.application.CustomerService;
import com.volter.shop.modules.customer.application.dto.CustomerCreateRequest;
import com.volter.shop.modules.customer.application.dto.CustomerUpdateRequest;
import com.volter.shop.modules.customer.domain.model.Customer;
import com.volter.shop.modules.customer.domain.repository.CustomerRepository;
import com.volter.shared.web.exception.BusinessRuleException;
import com.volter.shared.web.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private CustomerCreateRequest createRequest() {
        return new CustomerCreateRequest("Jane Doe", "1234567890123", "070111111", "071222222", "Street 1", "Skopje");
    }

    @Test
    @DisplayName("create rejects a duplicate national id")
    void create_duplicateNationalId_throws() {
        when(customerRepository.existsByNationalId("1234567890123")).thenReturn(true);

        assertThrows(BusinessRuleException.class, () -> customerService.create(createRequest()));
        verify(customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("create persists a customer built from the request")
    void create_persistsCustomer() {
        when(customerRepository.existsByNationalId("1234567890123")).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));

        customerService.create(createRequest());

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(captor.capture());
        Customer saved = captor.getValue();
        assertEquals("Jane Doe", saved.getFullName());
        assertEquals("1234567890123", saved.getNationalId());
        assertEquals("070111111", saved.getPhonePrimary());
        assertEquals("Skopje", saved.getCity());
    }

    @Test
    @DisplayName("update renames and updates the contact details of an existing customer")
    void update_appliesChanges() {
        Customer customer = mock(Customer.class);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        CustomerUpdateRequest request =
                new CustomerUpdateRequest("New Name", "079999999", null, "Avenue 2", "Bitola");

        customerService.update(1L, request);

        verify(customer).rename("New Name");
        verify(customer).updateContactDetails("079999999", null, "Avenue 2", "Bitola");
    }

    @Test
    @DisplayName("get throws when the customer is missing")
    void get_missing_throws() {
        when(customerRepository.findById(9L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> customerService.get(9L));
    }
}
