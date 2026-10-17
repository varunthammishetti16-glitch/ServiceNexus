
package com.servicenexus.controller;

import com.servicenexus.model.WorkerAvailability;
import com.servicenexus.model.WorkerProfile;
import com.servicenexus.repository.WorkerAvailabilityRepository;
import com.servicenexus.repository.WorkerProfileRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.DayOfWeek;

@Controller
@RequestMapping("/worker/availability")
public class WorkerAvailabilityController {

    private final WorkerAvailabilityRepository availabilityRepository;
    private final WorkerProfileRepository profileRepository;

    public WorkerAvailabilityController(
            WorkerAvailabilityRepository availabilityRepository,
            WorkerProfileRepository profileRepository) {
        this.availabilityRepository = availabilityRepository;
        this.profileRepository = profileRepository;
    }

    @GetMapping
    public String showAvailability(Model model) {
        WorkerProfile profile = getCurrentProfile();

        model.addAttribute("availability", new WorkerAvailability());
        model.addAttribute("days", DayOfWeek.values());
        model.addAttribute(
                "availabilityList",
                availabilityRepository
                        .findByWorkerProfileOrderByDayOfWeekAsc(profile)
        );

        return "worker-availability";
    }

    @PostMapping
    public String saveAvailability(
            @ModelAttribute("availability") WorkerAvailability availability,
            Model model,
            RedirectAttributes redirectAttributes) {

        WorkerProfile profile = getCurrentProfile();

        if (availability.getDayOfWeek() == null
                || availability.getStartTime() == null
                || availability.getEndTime() == null) {

            model.addAttribute(
                    "errorMessage",
                    "Please select a day and enter both times."
            );
            loadPageData(model, profile);
            return "worker-availability";
        }

        if (!availability.getEndTime()
                .isAfter(availability.getStartTime())) {

            model.addAttribute(
                    "errorMessage",
                    "End time must be later than start time."
            );
            loadPageData(model, profile);
            return "worker-availability";
        }

        availability.setWorkerProfile(profile);
        availabilityRepository.saveAndFlush(availability);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Availability saved successfully!"
        );

        return "redirect:/worker/availability";
    }

    private void loadPageData(Model model, WorkerProfile profile) {
        model.addAttribute("days", DayOfWeek.values());
        model.addAttribute(
                "availabilityList",
                availabilityRepository
                        .findByWorkerProfileOrderByDayOfWeekAsc(profile)
        );
    }

    private WorkerProfile getCurrentProfile() {
        return profileRepository.findAll().stream()
                .max((first, second) ->
                        first.getId().compareTo(second.getId()))
                .orElseThrow(() -> new IllegalStateException(
                        "Please create a worker profile first."
                ));
    }
}
