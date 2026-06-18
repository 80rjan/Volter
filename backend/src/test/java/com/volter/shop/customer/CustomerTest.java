package com.volter.shop.customer;

import com.volter.shop.modules.customer.domain.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = Customer.builder()
                .fullName("Original Name")
                .nationalId("1234567890123")
                .phonePrimary("070111111")
                .phoneSecondary("071222222")
                .address("Old Street 1")
                .city("Skopje")
                .build();
    }

    @Test
    @DisplayName("rename changes only the full name")
    void rename() {
        customer.rename("New Name");

        assertEquals("New Name", customer.getFullName());
        assertEquals("1234567890123", customer.getNationalId());
    }

    @Test
    @DisplayName("updateContactDetails replaces phones, address and city")
    void updateContactDetails() {
        customer.updateContactDetails("079999999", null, "New Avenue 2", "Bitola");

        assertEquals("079999999", customer.getPhonePrimary());
        assertNull(customer.getPhoneSecondary());
        assertEquals("New Avenue 2", customer.getAddress());
        assertEquals("Bitola", customer.getCity());
    }

    @Test
    @DisplayName("updateContactDetails leaves the national id untouched")
    void updateContactDetails_keepsNationalId() {
        customer.updateContactDetails("079999999", "078888888", "New Avenue 2", "Bitola");

        assertEquals("1234567890123", customer.getNationalId());
    }
}
