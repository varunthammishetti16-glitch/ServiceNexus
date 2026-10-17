
package com.servicenexus.controller;

import com.servicenexus.model.WorkerService;
import com.servicenexus.repository.WorkerServiceRepository;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class WorkerController {

    private final WorkerServiceRepository workerServiceRepository;

    public WorkerController(
            WorkerServiceRepository workerServiceRepository) {
        this.workerServiceRepository = workerServiceRepository;
    }

    // Display the worker dashboard and service listings
    @GetMapping("/worker")
    public String workerHome(Model model) {
        model.addAttribute(
                "services",
                workerServiceRepository.findAll()
        );
        return "worker";
    }

    // Add a new service
    @PostMapping("/worker/services")
    public String addService(
            @Valid @ModelAttribute("service") WorkerService service,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Please enter valid service details."
            );
            return "redirect:/worker";
        }

        try {
            workerServiceRepository.save(service);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Service added successfully!"
            );
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Could not save the service. Please check the application logs."
            );
        }

        return "redirect:/worker";
    }

    // Display the edit service form
    @GetMapping("/worker/services/{id}/edit")
    public String editServiceForm(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        WorkerService service = workerServiceRepository.findById(id)
                .orElse(null);

        if (service == null) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Service not found."
            );
            return "redirect:/worker";
        }

        if (!service.isActive()) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Inactive services cannot be edited."
            );
            return "redirect:/worker";
        }

        model.addAttribute("service", service);
        return "edit-service";
    }

    // Save changes to an existing service
    @PostMapping("/worker/services/{id}/edit")
    public String updateService(
            @PathVariable Long id,
            @Valid @ModelAttribute("service") WorkerService updatedService,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Please enter valid service details."
            );
            return "redirect:/worker";
        }

        try {
            WorkerService existingService = workerServiceRepository
                    .findById(id)
                    .orElseThrow(() ->
                            new IllegalArgumentException("Service not found"));

            if (!existingService.isActive()) {
                redirectAttributes.addFlashAttribute(
                        "error",
                        "Inactive services cannot be edited."
                );
                return "redirect:/worker";
            }

            existingService.setName(updatedService.getName());
            existingService.setCategory(updatedService.getCategory());
            existingService.setDescription(updatedService.getDescription());
            existingService.setPrice(updatedService.getPrice());
            existingService.setLocation(updatedService.getLocation());

            workerServiceRepository.save(existingService);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Service updated successfully!"
            );
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Could not update the service."
            );
        }

        return "redirect:/worker";
    }

    // Deactivate an existing service without deleting it
    @PostMapping("/worker/services/{id}/deactivate")
    public String deactivateService(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            WorkerService service = workerServiceRepository.findById(id)
                    .orElseThrow(() ->
                            new IllegalArgumentException("Service not found"));

            service.setActive(false);
            workerServiceRepository.save(service);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Service deactivated successfully!"
            );
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Could not deactivate the service."
            );
        }

        return "redirect:/worker";
    }
}
