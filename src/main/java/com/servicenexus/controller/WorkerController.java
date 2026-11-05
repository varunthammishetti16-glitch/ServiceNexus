package com.servicenexus.controller;

import com.servicenexus.entity.WorkerAccount;
import com.servicenexus.model.WorkerProfile;
import com.servicenexus.model.WorkerService;
import com.servicenexus.repository.WorkerAccountRepository;
import com.servicenexus.repository.WorkerServiceRepository;

import jakarta.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class WorkerController {

    private final WorkerServiceRepository workerServiceRepository;
    private final WorkerAccountRepository workerAccountRepository;

    public WorkerController(
            WorkerServiceRepository workerServiceRepository,
            WorkerAccountRepository workerAccountRepository) {
        this.workerServiceRepository = workerServiceRepository;
        this.workerAccountRepository = workerAccountRepository;
    }

    private WorkerProfile getAuthenticatedWorker(Authentication authentication) {
        if (authentication == null) {
            throw new IllegalStateException("Worker authentication required");
        }

        WorkerAccount account = workerAccountRepository
                .findByEmailWithWorkerProfile(authentication.getName())
                .orElseThrow(() ->
                        new IllegalStateException("Worker account not found"));

        if (!"APPROVED".equalsIgnoreCase(account.getVerificationStatus())) {
            throw new IllegalStateException("Worker account is not approved");
        }

        return account.getWorkerProfile();
    }

    @GetMapping("/worker")
    public String workerHome(
            Model model,
            Authentication authentication) {

        WorkerProfile worker = getAuthenticatedWorker(authentication);

        model.addAttribute(
                "services",
                workerServiceRepository.findByWorkerProfileId(worker.getId())
        );

        model.addAttribute("service", new WorkerService());

        return "worker";
    }

    @PostMapping("/worker/services")
    public String addService(
            @Valid @ModelAttribute("service") WorkerService service,
            BindingResult result,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                    "error", "Please enter valid service details.");
            return "redirect:/worker";
        }

        try {
            WorkerProfile worker = getAuthenticatedWorker(authentication);
            service.setWorkerProfile(worker);

            workerServiceRepository.save(service);

            redirectAttributes.addFlashAttribute(
                    "success", "Service added successfully!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "error", "Could not save the service.");
        }

        return "redirect:/worker";
    }

    @GetMapping("/worker/services/{id}/edit")
    public String editServiceForm(
            @PathVariable Long id,
            Model model,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        WorkerProfile worker = getAuthenticatedWorker(authentication);

        WorkerService service = workerServiceRepository
                .findByIdAndWorkerProfileId(id, worker.getId())
                .orElse(null);

        if (service == null || !service.isActive()) {
            redirectAttributes.addFlashAttribute(
                    "error", "Service not found or unavailable.");
            return "redirect:/worker";
        }

        model.addAttribute("service", service);
        return "edit-service";
    }

    @PostMapping("/worker/services/{id}/edit")
    public String updateService(
            @PathVariable Long id,
            @Valid @ModelAttribute("service") WorkerService updatedService,
            BindingResult result,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                    "error", "Please enter valid service details.");
            return "redirect:/worker";
        }

        try {
            WorkerProfile worker = getAuthenticatedWorker(authentication);

            WorkerService existingService = workerServiceRepository
                    .findByIdAndWorkerProfileId(id, worker.getId())
                    .orElseThrow(() ->
                            new IllegalArgumentException("Service not found"));

            if (!existingService.isActive()) {
                redirectAttributes.addFlashAttribute(
                        "error", "Inactive services cannot be edited.");
                return "redirect:/worker";
            }

            existingService.setName(updatedService.getName());
            existingService.setCategory(updatedService.getCategory());
            existingService.setDescription(updatedService.getDescription());
            existingService.setPrice(updatedService.getPrice());
            existingService.setLocation(updatedService.getLocation());

            workerServiceRepository.save(existingService);

            redirectAttributes.addFlashAttribute(
                    "success", "Service updated successfully!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "error", "Could not update the service.");
        }

        return "redirect:/worker";
    }

    @PostMapping("/worker/services/{id}/deactivate")
    public String deactivateService(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            WorkerProfile worker = getAuthenticatedWorker(authentication);

            WorkerService service = workerServiceRepository
                    .findByIdAndWorkerProfileId(id, worker.getId())
                    .orElseThrow(() ->
                            new IllegalArgumentException("Service not found"));

            service.setActive(false);
            workerServiceRepository.save(service);

            redirectAttributes.addFlashAttribute(
                    "success", "Service deactivated successfully!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "error", "Could not deactivate the service.");
        }

        return "redirect:/worker";
    }
}