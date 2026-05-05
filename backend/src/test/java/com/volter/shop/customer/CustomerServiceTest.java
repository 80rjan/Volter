package com.volter.shop.customer;

import com.volter.shop.modules.customer.application.CustomerService;
import com.volter.shop.modules.customer.application.dto.CustomerFilterDTO;
import com.volter.shop.modules.customer.domain.model.Customer;
import com.volter.shop.modules.customer.domain.model.enums.CustomerRiskLevel;
import com.volter.shop.modules.customer.domain.repository.CustomerRepository;
import com.volter.shop.shared.common.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = mock(Customer.class);
        lenient().when(customer.getId()).thenReturn(1L);
    }

    // =========================================================================
    // getById()
    // =========================================================================

    @Nested
    @DisplayName("getById()")
    class GetById {

        @Test
        @DisplayName("returns customer when found")
        void returnsCustomer_whenFound() {
            when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

            assertEquals(customer, customerService.getById(1L));
            verify(customerRepository).findById(1L);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when not found")
        void throwsResourceNotFoundException_whenNotFound() {
            when(customerRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> customerService.getById(99L));
        }
    }

    // =========================================================================
    // getReferenceById()
    // =========================================================================

    @Nested
    @DisplayName("getReferenceById()")
    class GetReferenceById {

        @Test
        @DisplayName("delegates to repository and returns the proxy")
        void delegatesToRepository() {
            when(customerRepository.getReferenceById(1L)).thenReturn(customer);

            assertEquals(customer, customerService.getReferenceById(1L));
            verify(customerRepository).getReferenceById(1L);
        }
    }

    // =========================================================================
    // save()
    // =========================================================================

    @Nested
    @DisplayName("save()")
    class Save {

        @Test
        @DisplayName("delegates to repository and returns saved entity")
        void delegatesToRepository() {
            when(customerRepository.save(customer)).thenReturn(customer);

            assertEquals(customer, customerService.save(customer));
            verify(customerRepository).save(customer);
        }
    }

    // =========================================================================
    // getAll()
    // =========================================================================

    @Nested
    @DisplayName("getAll()")
    class GetAll {

        @Test
        @DisplayName("returns page from repository")
        void returnsPage() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Customer> page = new PageImpl<>(List.of(customer), pageable, 1);
            when(customerRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

            Page<Customer> result = customerService.getAll(new CustomerFilterDTO(), pageable);

            assertEquals(1, result.getTotalElements());
            verify(customerRepository).findAll(any(Specification.class), eq(pageable));
        }

        @Test
        @DisplayName("returns empty page when no customers match")
        void emptyResult_returnsEmptyPage() {
            Pageable pageable = PageRequest.of(0, 10);
            when(customerRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(Page.empty(pageable));

            assertTrue(customerService.getAll(new CustomerFilterDTO(), pageable).isEmpty());
        }

        @Test
        @DisplayName("filter with name is forwarded to specification")
        void nameFilter_passedToSpec() {
            Pageable pageable = PageRequest.of(0, 10);
            CustomerFilterDTO filter = new CustomerFilterDTO();
            filter.setName("Alice");
            Page<Customer> page = new PageImpl<>(List.of(customer));
            when(customerRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

            assertEquals(1, customerService.getAll(filter, pageable).getTotalElements());
        }

        @Test
        @DisplayName("filter with riskLevel is forwarded to specification")
        void riskLevelFilter_passedToSpec() {
            Pageable pageable = PageRequest.of(0, 10);
            CustomerFilterDTO filter = new CustomerFilterDTO();
            filter.setRiskLevel(CustomerRiskLevel.HIGH);
            when(customerRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(Page.empty());

            customerService.getAll(filter, pageable);

            verify(customerRepository).findAll(any(Specification.class), eq(pageable));
        }

        @Test
        @DisplayName("second page returns correct pagination metadata")
        void pagination_secondPage() {
            Pageable pageable = PageRequest.of(1, 5);
            Page<Customer> page = new PageImpl<>(List.of(customer), pageable, 10);
            when(customerRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

            Page<Customer> result = customerService.getAll(new CustomerFilterDTO(), pageable);

            assertEquals(1, result.getNumber());
            assertEquals(10, result.getTotalElements());
            assertEquals(2, result.getTotalPages());
        }

        @Test
        @DisplayName("sorted pageable is forwarded to repository")
        void sortedPageable_forwardedToRepository() {
            Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
            when(customerRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(Page.empty());

            customerService.getAll(new CustomerFilterDTO(), pageable);

            verify(customerRepository).findAll(any(Specification.class), eq(pageable));
        }
    }

    // =========================================================================
    // updateTelephone()
    // =========================================================================

    @Nested
    @DisplayName("updateTelephone()")
    class UpdateTelephone {

        @Test
        @DisplayName("updates both phone numbers and saves")
        void updatesPhoneNumbers_andSaves() {
            Customer real = Customer.builder()
                    .name("Alice").phoneNumber("070111111").embg("1234567890123")
                    .address("St 1").city("Skopje").build();
            when(customerRepository.findById(1L)).thenReturn(Optional.of(real));
            when(customerRepository.save(real)).thenReturn(real);

            Customer result = customerService.updateTelephone(1L, "070999999", "070888888");

            assertEquals("070999999", result.getPhoneNumber());
            assertEquals("070888888", result.getReservePhoneNumber());
            verify(customerRepository).save(real);
        }

        @Test
        @DisplayName("updates to null reserve phone number")
        void updatesReservePhoneNumber_toNull() {
            Customer real = Customer.builder()
                    .name("Bob").phoneNumber("070222222").embg("9876543210987")
                    .address("St 2").city("Bitola").build();
            when(customerRepository.findById(2L)).thenReturn(Optional.of(real));
            when(customerRepository.save(real)).thenReturn(real);

            Customer result = customerService.updateTelephone(2L, "070333333", null);

            assertNull(result.getReservePhoneNumber());
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when customer not found")
        void throwsResourceNotFoundException_whenNotFound() {
            when(customerRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> customerService.updateTelephone(99L, "070000000", null));
        }

        @Test
        @DisplayName("saves exactly once")
        void savesExactlyOnce() {
            Customer real = Customer.builder()
                    .name("Carol").phoneNumber("070444444").embg("1111111111111")
                    .address("St 3").city("Ohrid").build();
            when(customerRepository.findById(3L)).thenReturn(Optional.of(real));
            when(customerRepository.save(real)).thenReturn(real);

            customerService.updateTelephone(3L, "070555555", "070666666");

            verify(customerRepository, times(1)).save(real);
        }
    }
}
