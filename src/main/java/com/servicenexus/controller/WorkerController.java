
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class WorkerController {

    private final WorkerServiceRepository workerServiceRepository;

    public WorkerController(
            WorkerServiceRepository workerServiceRepository) {
        this.workerServiceRepository = workerServiceRepository;
    }

    @GetMapping("/worker")
    public String workerHome(Model model) {
        model.addAttribute(
                "services",
                workerServiceRepository.findAll()
        );
        return "worker";
    }

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
}
