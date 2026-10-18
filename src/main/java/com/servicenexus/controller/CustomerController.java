
package com.servicenexus.controller;

import com.servicenexus.dto.CustomerRegistrationRequest;
import com.servicenexus.dto.CustomerRegistrationResponse;
import com.servicenexus.service.CustomerService;
import com.servicenexus.dto.CustomerProfileUpdateRequest;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/register")
    public ResponseEntity<CustomerRegistrationResponse> register(
            @Valid @RequestBody CustomerRegistrationRequest request) {

        CustomerRegistrationResponse response =
                customerService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomerProfile(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                customerService.getCustomerProfile(id)
        );
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CustomerRegistrationResponse> updateCustomerProfile(
            @PathVariable Long id,
            @Valid @RequestBody CustomerProfileUpdateRequest request) {

        CustomerRegistrationResponse response =
                customerService.updateCustomerProfile(id, request);

        return ResponseEntity.ok(response);
    }

}
