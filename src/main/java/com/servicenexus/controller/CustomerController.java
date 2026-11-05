
package com.servicenexus.controller;

import com.servicenexus.dto.CustomerProfileUpdateRequest;
import com.servicenexus.dto.CustomerRegistrationRequest;
import com.servicenexus.dto.CustomerRegistrationResponse;
import com.servicenexus.dto.ChangePasswordRequest;
import com.servicenexus.dto.CreateBookingRequest;
import com.servicenexus.dto.BookingResponse;
import com.servicenexus.dto.AvailableServiceResponse;
import com.servicenexus.dto.CustomerDashboardResponse;

import com.servicenexus.service.CustomerService;
import com.servicenexus.service.BookingService;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final BookingService bookingService;

    public CustomerController(
            CustomerService customerService,
            BookingService bookingService) {

        this.customerService = customerService;
        this.bookingService = bookingService;
    }

    // CUSTOMER REGISTRATION
    @PostMapping("/register")
    public ResponseEntity<CustomerRegistrationResponse> register(
            @Valid @RequestBody CustomerRegistrationRequest request) {

        CustomerRegistrationResponse response =
                customerService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET LOGGED-IN CUSTOMER PROFILE
    @GetMapping("/me")
    public ResponseEntity<CustomerRegistrationResponse> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails) {

        CustomerRegistrationResponse response =
                customerService.getCustomerProfileByEmail(
                        userDetails.getUsername());

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
                        userDetails.getUsername());

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
                        userDetails.getUsername());

        return ResponseEntity.ok(response);
    }

    // CHANGE CUSTOMER PASSWORD
    @PutMapping("/me/change-password")
    public ResponseEntity<String> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        customerService.changePassword(
                request,
                userDetails.getUsername());

        return ResponseEntity.ok("Password changed successfully");
    }

    // CREATE CUSTOMER BOOKING
    @PostMapping("/me/bookings")
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody CreateBookingRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        BookingResponse response =
                bookingService.createBooking(
                        request,
                        userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET CUSTOMER BOOKING HISTORY
    @GetMapping("/me/bookings")
    public ResponseEntity<List<BookingResponse>> getMyBookings(
            @AuthenticationPrincipal UserDetails userDetails) {

        List<BookingResponse> bookings =
                bookingService.getMyBookings(
                        userDetails.getUsername());

        return ResponseEntity.ok(bookings);
    }

    // GET CUSTOMER DASHBOARD STATISTICS
    @GetMapping("/me/dashboard")
    public ResponseEntity<CustomerDashboardResponse> getCustomerDashboard(
            @AuthenticationPrincipal UserDetails userDetails) {

        CustomerDashboardResponse response =
                bookingService.getCustomerDashboard(
                        userDetails.getUsername());

        return ResponseEntity.ok(response);
    }

    // CANCEL CUSTOMER BOOKING
    @PutMapping("/me/bookings/{bookingId}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal UserDetails userDetails) {

        BookingResponse response =
                bookingService.cancelBooking(
                        bookingId,
                        userDetails.getUsername());

        return ResponseEntity.ok(response);
    }

    // GET ACTIVE SERVICES FOR CUSTOMER BOOKING
    @GetMapping("/me/available-services")
    public ResponseEntity<List<AvailableServiceResponse>> getAvailableServices() {

        List<AvailableServiceResponse> services =
                bookingService.getAvailableServices();

        return ResponseEntity.ok(services);
    }
}
