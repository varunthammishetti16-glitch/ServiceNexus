
package com.servicenexus.controller;

import com.servicenexus.dto.CustomerProfileUpdateRequest;
import com.servicenexus.dto.CustomerRegistrationRequest;
import com.servicenexus.dto.CustomerRegistrationResponse;
import com.servicenexus.service.CustomerService;
import com.servicenexus.dto.ChangePasswordRequest;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // CUSTOMER REGISTRATION
    @PostMapping("/register")
    public ResponseEntity<CustomerRegistrationResponse> register(
            @Valid @RequestBody CustomerRegistrationRequest request) {

        CustomerRegistrationResponse response =
                customerService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // GET LOGGED-IN CUSTOMER PROFILE
    @GetMapping("/me")
    public ResponseEntity<CustomerRegistrationResponse> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails) {

        CustomerRegistrationResponse response =
                customerService.getCustomerProfileByEmail(
                        userDetails.getUsername()
                );

        return ResponseEntity.ok(response);
    }

    // GET CUSTOMER PROFILE BY ID
    @GetMapping("/{id}")
    public ResponseEntity<CustomerRegistrationResponse> getCustomerProfile(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        CustomerRegistrationResponse response =
                customerService.getCustomerProfile(
                        id,
                        userDetails.getUsername()
                );

        return ResponseEntity.ok(response);
    }

    // UPDATE CUSTOMER PROFILE
    @PutMapping("/{id}")
    public ResponseEntity<CustomerRegistrationResponse> updateCustomerProfile(
            @PathVariable Long id,
            @Valid @RequestBody CustomerProfileUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        CustomerRegistrationResponse response =
                customerService.updateCustomerProfile(
                        id,
                        request,
                        userDetails.getUsername()
                );

        return ResponseEntity.ok(response);
    }
    // CHANGE CUSTOMER PASSWORD
@PutMapping("/me/change-password")
public ResponseEntity<String> changePassword(
        @Valid @RequestBody ChangePasswordRequest request,
        @AuthenticationPrincipal UserDetails userDetails) {

    customerService.changePassword(
            request,
            userDetails.getUsername()
    );

    return ResponseEntity.ok("Password changed successfully");
}
}
