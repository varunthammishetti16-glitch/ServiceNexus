
package com.servicenexus.controller;

import com.servicenexus.dto.WorkerRegistrationRequest;
import com.servicenexus.service.WorkerRegistrationService;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class WorkerRegistrationController {

    private final WorkerRegistrationService registrationService;

    public WorkerRegistrationController(
            WorkerRegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping("/worker/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("request",
                new WorkerRegistrationRequest(
                        "", "", "", "", "", "", ""));
        return "worker-register";
    }

    @PostMapping("/worker/register")
    public String registerWorker(
            @Valid @ModelAttribute("request")
            WorkerRegistrationRequest request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "worker-register";
        }

        try {
            registrationService.register(request);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Registration successful! Please log in.");
            return "redirect:/login";

        } catch (IllegalArgumentException exception) {
            model.addAttribute("errorMessage", exception.getMessage());
            return "worker-register";
        }
    }
}
