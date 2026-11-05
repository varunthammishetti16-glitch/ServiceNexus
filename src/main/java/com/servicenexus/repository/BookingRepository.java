package com.servicenexus.repository;

import com.servicenexus.model.Booking;
import com.servicenexus.model.BookingStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Customer bookings
    List<Booking> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    long countByCustomerId(Long customerId);

    long countByCustomerIdAndStatus(
            Long customerId,
            BookingStatus status
    );

    List<Booking> findByCustomerIdAndStatusOrderByCreatedAtDesc(
            Long customerId,
            BookingStatus status
    );

    // Bookings belonging to a particular worker
    @Query("""
        SELECT b FROM Booking b
        JOIN FETCH b.service s
        WHERE s.workerProfile.id = :workerProfileId
        ORDER BY b.createdAt DESC
        """)
    List<Booking> findWorkerBookings(
            @Param("workerProfileId") Long workerProfileId
    );

    // Find a booking only if it belongs to the authenticated worker
    @Query("""
        SELECT b FROM Booking b
        JOIN FETCH b.service s
        WHERE b.id = :bookingId
        AND s.workerProfile.id = :workerProfileId
        """)
    Optional<Booking> findWorkerBooking(
            @Param("bookingId") Long bookingId,
            @Param("workerProfileId") Long workerProfileId
    );
}