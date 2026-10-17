
package com.servicenexus.service;

import com.servicenexus.model.WorkerEarning;
import com.servicenexus.repository.WorkerEarningRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class WorkerEarningService {

    private final WorkerEarningRepository earningRepository;

    public WorkerEarningService(
            WorkerEarningRepository earningRepository) {
        this.earningRepository = earningRepository;
    }

    @Transactional
    public WorkerEarning recordCompletedBookingEarning(
            Long bookingId,
            Long workerProfileId,
            BigDecimal amount) {

        // Validate booking ID
        if (bookingId == null || bookingId <= 0) {
            throw new IllegalArgumentException(
                    "Booking ID must be valid.");
        }

        // Validate worker profile ID
        if (workerProfileId == null || workerProfileId <= 0) {
            throw new IllegalArgumentException(
                    "Worker profile ID must be valid.");
        }

        // Validate earning amount
        if (amount == null
                || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Earning amount cannot be negative or null.");
        }

        // Check whether this booking already has an earning
        var existingEarning =
                earningRepository.findByBookingId(bookingId);

        if (existingEarning.isPresent()) {
            WorkerEarning existing = existingEarning.get();

            // Reject conflicting worker or amount details
            if (!existing.getWorkerProfileId().equals(workerProfileId)
                    || existing.getAmount().compareTo(amount) != 0) {
                throw new IllegalStateException(
                        "This booking already has a different earning record.");
            }

            // Return the existing record if details match
            return existing;
        }

        // Create a new earning record
        WorkerEarning earning = new WorkerEarning();
        earning.setBookingId(bookingId);
        earning.setWorkerProfileId(workerProfileId);
        earning.setAmount(amount);
        earning.setEarnedAt(LocalDateTime.now());

        return earningRepository.save(earning);
    }
}
