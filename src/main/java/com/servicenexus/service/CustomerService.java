
package com.servicenexus.service;

import com.servicenexus.dto.CustomerRegistrationRequest;
import com.servicenexus.dto.ChangePasswordRequest;
import com.servicenexus.dto.CustomerRegistrationResponse;
import com.servicenexus.dto.CustomerProfileUpdateRequest;
import com.servicenexus.entity.Customer;
import com.servicenexus.repository.CustomerRepository;

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

    // CUSTOMER REGISTRATION
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

            return toResponse(saved);

        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "An account with this email already exists"
            );
        }
    }

    // VIEW CUSTOMER PROFILE BY ID
    @Transactional(readOnly = true)
    public CustomerRegistrationResponse getCustomerProfile(
            Long id,
            String authenticatedEmail) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Customer not found"
                ));

        verifyOwnership(customer, authenticatedEmail);

        return toResponse(customer);
    }

    // VIEW LOGGED-IN CUSTOMER PROFILE BY EMAIL
    @Transactional(readOnly = true)
    public CustomerRegistrationResponse getCustomerProfileByEmail(
            String email) {

        Customer customer = customerRepository
                .findByEmailIgnoreCase(email.trim())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Customer not found"
                ));

        return toResponse(customer);
    }

    // UPDATE CUSTOMER PROFILE
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

        verifyOwnership(customer, authenticatedEmail);

        String email = request.email().trim().toLowerCase();

        boolean emailUsedByAnotherCustomer =
                customerRepository.findByEmailIgnoreCase(email)
                        .filter(existing ->
                                !existing.getId().equals(id))
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

        return toResponse(saved);
    }

    // CHANGE CUSTOMER PASSWORD
@Transactional
public void changePassword(
        ChangePasswordRequest request,
        String authenticatedEmail) {

    Customer customer = customerRepository
            .findByEmailIgnoreCase(authenticatedEmail.trim())
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Customer not found"
            ));

    if (!passwordEncoder.matches(
            request.currentPassword(),
            customer.getPasswordHash())) {

        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Current password is incorrect"
        );
    }

    if (!request.newPassword().equals(request.confirmPassword())) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "New password and confirmation do not match"
        );
    }

    if (passwordEncoder.matches(
            request.newPassword(),
            customer.getPasswordHash())) {

        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "New password must be different from current password"
        );
    }

    customer.setPasswordHash(
            passwordEncoder.encode(request.newPassword())
    );

    customerRepository.save(customer);
}
    // CHECK PROFILE OWNERSHIP
    private void verifyOwnership(
            Customer customer,
            String authenticatedEmail) {

        if (!customer.getEmail().equalsIgnoreCase(authenticatedEmail)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You cannot access another customer's profile"
            );
        }
    }

    // CONVERT CUSTOMER ENTITY TO RESPONSE DTO
    private CustomerRegistrationResponse toResponse(Customer customer) {

        return new CustomerRegistrationResponse(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getAddress(),
                customer.getCreatedAt()
        );
    }

    // HANDLE OPTIONAL FIELDS
    private String normalizeOptional(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}
