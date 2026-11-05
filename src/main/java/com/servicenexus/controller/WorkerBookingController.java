package com.servicenexus.controller;

import com.servicenexus.entity.WorkerAccount;
import com.servicenexus.model.WorkerProfile;
import com.servicenexus.repository.WorkerAccountRepository;
import com.servicenexus.service.WorkerBookingService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class WorkerBookingController {

    private final WorkerBookingService workerBookingService;
    private final WorkerAccountRepository workerAccountRepository;

    public WorkerBookingController(
            WorkerBookingService workerBookingService,
            WorkerAccountRepository workerAccountRepository) {

        this.workerBookingService = workerBookingService;
        this.workerAccountRepository = workerAccountRepository;
    }

    // Identify the currently logged-in, approved worker
    private WorkerProfile getAuthenticatedWorker(
            Authentication authentication) {

        if (authentication == null) {
            throw new IllegalStateException(
                    "Worker authentication required.");
        }

        WorkerAccount account = workerAccountRepository
                .findByEmailWithWorkerProfile(authentication.getName())
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Worker account not found."));

        if (!"APPROVED".equalsIgnoreCase(
                account.getVerificationStatus())) {

            throw new IllegalStateException(
                    "Your worker account is not approved.");
        }

        return account.getWorkerProfile();
    }

    // Display bookings belonging to the logged-in worker
    @GetMapping("/worker/bookings")
    public String showBookings(
            Model model,
            Authentication authentication) {

        WorkerProfile worker = getAuthenticatedWorker(authentication);

        model.addAttribute(
                "bookings",
                workerBookingService.getWorkerBookings(worker.getId())
        );

        model.addAttribute("workerProfile", worker);

        return "worker-bookings";
    }

    // Accept booking
    @PostMapping("/worker/bookings/{id}/accept")
    public String acceptBooking(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            WorkerProfile worker =
                    getAuthenticatedWorker(authentication);

            workerBookingService.acceptBooking(id, worker.getId());

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Booking accepted successfully.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage());
        }

        return "redirect:/worker/bookings";
    }

    // Reject booking
    @PostMapping("/worker/bookings/{id}/reject")
    public String rejectBooking(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            WorkerProfile worker =
                    getAuthenticatedWorker(authentication);

            workerBookingService.rejectBooking(id, worker.getId());

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Booking rejected successfully.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage());
        }

        return "redirect:/worker/bookings";
    }

    // Complete booking and record earnings
    @PostMapping("/worker/bookings/{id}/complete")
    public String completeBooking(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            WorkerProfile worker =
                    getAuthenticatedWorker(authentication);

            workerBookingService.completeBooking(id, worker.getId());

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Booking completed and earnings recorded successfully.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage());
        }

        return "redirect:/worker/bookings";
    }
}