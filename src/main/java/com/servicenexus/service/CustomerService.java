package com.servicenexus.service;

import com.servicenexus.dto.CustomerRegistrationRequest;
import com.servicenexus.dto.CustomerRegistrationResponse;
import com.servicenexus.entity.Customer;
import com.servicenexus.repository.CustomerRepository;
import com.servicenexus.dto.CustomerProfileUpdateRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerService(
            CustomerRepository customerRepository,
            PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public CustomerRegistrationResponse register(
            CustomerRegistrationRequest request) {

        String email = request.email().trim().toLowerCase();

        if (customerRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "An account with this email already exists"
            );
        }

        Customer customer = new Customer();
        customer.setName(request.name().trim());
        customer.setEmail(email);
        customer.setPasswordHash(
            passwordEncoder.encode(request.password())
        );
        customer.setPhone(normalizeOptional(request.phone()));
        customer.setAddress(normalizeOptional(request.address()));

        try {
            Customer saved = customerRepository.saveAndFlush(customer);

            return new CustomerRegistrationResponse(
                saved.getId(),
                saved.getName(),
                saved.getEmail(),
                saved.getPhone(),
                saved.getAddress(),
                saved.getCreatedAt()
            );
        } catch (DataIntegrityViolationException ex) {
            // Also protects against simultaneous registrations using
            // the same email address.
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "An account with this email already exists"
            );
        }
    }
    

@Transactional(readOnly = true)
public CustomerRegistrationResponse getCustomerProfile(
        Long id,
        String authenticatedEmail) {

    Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Customer not found"
            ));

    if (!customer.getEmail().equalsIgnoreCase(authenticatedEmail)) {
        throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "You cannot access another customer's profile"
        );
    }

    return new CustomerRegistrationResponse(
            customer.getId(),
            customer.getName(),
            customer.getEmail(),
            customer.getPhone(),
            customer.getAddress(),
            customer.getCreatedAt()
    );
}

@Transactional
public CustomerRegistrationResponse updateCustomerProfile(
        Long id,
        CustomerProfileUpdateRequest request,
        String authenticatedEmail) {

    Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Customer not found"
            ));

    if (!customer.getEmail().equalsIgnoreCase(authenticatedEmail)) {
        throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "You cannot update another customer's profile"
        );
    }

    String email = request.email().trim().toLowerCase();

    boolean emailUsedByAnotherCustomer =
            customerRepository.findByEmailIgnoreCase(email)
                    .filter(existing -> !existing.getId().equals(id))
                    .isPresent();

    if (emailUsedByAnotherCustomer) {
        throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "An account with this email already exists"
        );
    }

    customer.setName(request.name().trim());
    customer.setEmail(email);
    customer.setPhone(normalizeOptional(request.phone()));
    customer.setAddress(normalizeOptional(request.address()));

    Customer saved = customerRepository.save(customer);

    return new CustomerRegistrationResponse(
            saved.getId(),
            saved.getName(),
            saved.getEmail(),
            saved.getPhone(),
            saved.getAddress(),
            saved.getCreatedAt()
    );
}
    private String normalizeOptional(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}