package com.servicenexus.service;

import com.servicenexus.model.Booking;
import com.servicenexus.model.BookingStatus;
import com.servicenexus.repository.BookingRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WorkerBookingService {

    private final BookingRepository bookingRepository;
    private final WorkerEarningService earningService;

    public WorkerBookingService(
            BookingRepository bookingRepository,
            WorkerEarningService earningService) {

        this.bookingRepository = bookingRepository;
        this.earningService = earningService;
    }

    // View bookings belonging to the authenticated worker
    @Transactional(readOnly = true)
    public List<Booking> getWorkerBookings(Long workerProfileId) {

        return bookingRepository.findWorkerBookings(workerProfileId);
    }

    // Accept a pending booking
    @Transactional
    public Booking acceptBooking(
            Long bookingId,
            Long workerProfileId) {

        Booking booking = getOwnedBooking(bookingId, workerProfileId);

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException(
                    "Only pending bookings can be accepted.");
        }

        booking.setStatus(BookingStatus.ACCEPTED);

        return bookingRepository.save(booking);
    }

    // Reject a pending booking
    @Transactional
    public Booking rejectBooking(
            Long bookingId,
            Long workerProfileId) {

        Booking booking = getOwnedBooking(bookingId, workerProfileId);

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException(
                    "Only pending bookings can be rejected.");
        }

        booking.setStatus(BookingStatus.REJECTED);

        return bookingRepository.save(booking);
    }

    // Complete an accepted booking and record its earnings
    @Transactional
    public Booking completeBooking(
            Long bookingId,
            Long workerProfileId) {

        Booking booking = getOwnedBooking(bookingId, workerProfileId);

        // Handle repeated completion requests safely
        if (booking.getStatus() == BookingStatus.COMPLETED) {

            earningService.recordCompletedBookingEarning(
                    booking.getId(),
                    workerProfileId,
                    booking.getPrice()
            );

            return booking;
        }

        if (booking.getStatus() != BookingStatus.ACCEPTED) {
            throw new IllegalStateException(
                    "Only accepted bookings can be completed.");
        }

        // Record earnings using the original booking price
        earningService.recordCompletedBookingEarning(
                booking.getId(),
                workerProfileId,
                booking.getPrice()
        );

        // Update booking status
        booking.setStatus(BookingStatus.COMPLETED);

        return bookingRepository.save(booking);
    }

    // Ownership verification
    private Booking getOwnedBooking(
            Long bookingId,
            Long workerProfileId) {

        if (bookingId == null || workerProfileId == null) {
            throw new IllegalArgumentException(
                    "Booking ID and worker profile ID are required.");
        }

        return bookingRepository
                .findWorkerBooking(bookingId, workerProfileId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Booking not found or does not belong to you."));
    }
}