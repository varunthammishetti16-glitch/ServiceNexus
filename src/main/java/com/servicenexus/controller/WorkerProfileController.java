
package com.servicenexus.controller;

import com.servicenexus.entity.WorkerAccount;
import com.servicenexus.model.WorkerProfile;
import com.servicenexus.repository.WorkerAccountRepository;
import com.servicenexus.repository.WorkerProfileRepository;

import jakarta.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class WorkerProfileController {

    private final WorkerProfileRepository workerProfileRepository;
    private final WorkerAccountRepository workerAccountRepository;

    public WorkerProfileController(
            WorkerProfileRepository workerProfileRepository,
            WorkerAccountRepository workerAccountRepository) {

        this.workerProfileRepository = workerProfileRepository;
        this.workerAccountRepository = workerAccountRepository;
    }

    // Display only the logged-in worker's profile
    @GetMapping("/worker/profile")
    public String showProfileForm(
            Authentication authentication,
            Model model) {

        WorkerAccount account = workerAccountRepository
                .findByEmailWithWorkerProfile(authentication.getName())
                .orElse(null);

        if (account == null) {
            return "redirect:/login";
        }

        model.addAttribute("profile", account.getWorkerProfile());

        return "worker-profile";
    }

    // Update only the logged-in worker's linked profile
    @PostMapping("/worker/profile")
    public String saveProfile(
            @Valid @ModelAttribute("profile") WorkerProfile profile,
            BindingResult result,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        WorkerAccount account = workerAccountRepository
                .findByEmailWithWorkerProfile(authentication.getName())
                .orElse(null);

        if (account == null) {
            return "redirect:/login";
        }

        if (result.hasErrors()) {
            return "worker-profile";
        }

        WorkerProfile existingProfile = account.getWorkerProfile();

        existingProfile.setName(profile.getName());
        existingProfile.setPhone(profile.getPhone());
        existingProfile.setSkills(profile.getSkills());
        existingProfile.setLocation(profile.getLocation());
        existingProfile.setDescription(profile.getDescription());

        workerProfileRepository.save(existingProfile);

        redirectAttributes.addFlashAttribute(
                "success",
                "Worker profile updated successfully!");

        return "redirect:/worker/profile";
    }
}
