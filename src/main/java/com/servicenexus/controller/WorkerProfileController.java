
package com.servicenexus.controller;

import com.servicenexus.model.WorkerProfile;
import com.servicenexus.repository.WorkerProfileRepository;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.List;

@Controller
public class WorkerProfileController {

    private final WorkerProfileRepository workerProfileRepository;

    public WorkerProfileController(
            WorkerProfileRepository workerProfileRepository) {
        this.workerProfileRepository = workerProfileRepository;
    }

    // Display the latest saved worker profile and print debug information
    @GetMapping("/worker/profile")
    public String showProfileForm(Model model) {

        List<WorkerProfile> profiles =
                workerProfileRepository.findAll();

        System.out.println("========== PROFILE DEBUG ==========");
        System.out.println("Profiles found in database: " + profiles.size());

        WorkerProfile profile = profiles.stream()
                .filter(p -> p.getId() != null)
                .max(Comparator.comparing(WorkerProfile::getId))
                .orElseGet(WorkerProfile::new);

        System.out.println("Profile ID loaded: " + profile.getId());
        System.out.println("Profile name loaded: " + profile.getName());
        System.out.println("===================================");

        model.addAttribute("profile", profile);

        return "worker-profile";
    }

    // Save a new profile or update the latest existing profile
    @PostMapping("/worker/profile")
    public String saveProfile(
            @Valid @ModelAttribute("profile") WorkerProfile profile,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            System.out.println("Profile validation failed:");
            result.getAllErrors().forEach(System.out::println);
            return "worker-profile";
        }

        List<WorkerProfile> profiles =
                workerProfileRepository.findAll();

        WorkerProfile existingProfile = profiles.stream()
                .filter(p -> p.getId() != null)
                .max(Comparator.comparing(WorkerProfile::getId))
                .orElse(null);

        if (existingProfile != null) {
            existingProfile.setName(profile.getName());
            existingProfile.setPhone(profile.getPhone());
            existingProfile.setSkills(profile.getSkills());
            existingProfile.setLocation(profile.getLocation());
            existingProfile.setDescription(profile.getDescription());

            workerProfileRepository.save(existingProfile);

        } else {
            workerProfileRepository.save(profile);
        }

        redirectAttributes.addFlashAttribute(
                "success",
                "Worker profile saved successfully!");

        return "redirect:/worker/profile";
    }
}
