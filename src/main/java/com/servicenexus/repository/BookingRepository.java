
package com.servicenexus.repository;

import com.servicenexus.model.Booking;
import com.servicenexus.model.BookingStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Get all bookings belonging to a customer, newest first
    List<Booking> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    // Count all bookings belonging to a customer
    long countByCustomerId(Long customerId);

    // Count customer bookings by status
    long countByCustomerIdAndStatus(
            Long customerId,
            BookingStatus status
    );

    // Get a customer's bookings by status, newest first
    List<Booking> findByCustomerIdAndStatusOrderByCreatedAtDesc(
            Long customerId,
            BookingStatus status
    );
}
