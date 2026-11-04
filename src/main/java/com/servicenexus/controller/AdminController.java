
package com.servicenexus.controller;
import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.servicenexus.cooperative.dto.CreateCooperativeRequest;
import com.servicenexus.cooperative.entity.Cooperative;
import com.servicenexus.cooperative.service.CooperativeService;
import com.servicenexus.model.WorkerProfile;
import com.servicenexus.repository.WorkerProfileRepository;

import jakarta.validation.Valid;



@Controller
public class AdminController {

     private final WorkerProfileRepository workerProfileRepository;
    private final CooperativeService cooperativeService;

 public AdminController(
        CooperativeService cooperativeService,
        WorkerProfileRepository workerProfileRepository) {

    this.cooperativeService = cooperativeService;
    this.workerProfileRepository = workerProfileRepository;
}

@GetMapping("/admin/dashboard")
public String showDashboard(Model model) {

    // Customer module is not implemented yet.
    model.addAttribute("customerCount", 0);

    // Get the actual number of workers from MySQL.
    model.addAttribute(
            "workerCount",
            workerProfileRepository.count()
    );

    // Booking module is not implemented yet.
    model.addAttribute("bookingCount", 0);

    // Get the actual number of cooperatives from MySQL.
    model.addAttribute(
            "cooperativeCount",
            cooperativeService.findAll().size()
    );
model.addAttribute(
        "recentCooperatives",
        cooperativeService.findRecent(3)
);
    return "admin-dashboard";
}

    // Customer Management
    @GetMapping("/admin/customers")
    public String showCustomers(Model model) {

        model.addAttribute("customers", Collections.emptyList());
        model.addAttribute("customerCount", 0);

        return "admin-customers";
    }

    // Worker Management
     @GetMapping("/admin/workers")
public String showWorkers(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String location,
        Model model) {

    List<WorkerProfile> allWorkers = workerProfileRepository.findAll();

    List<WorkerProfile> filteredWorkers = allWorkers.stream()
            .filter(worker -> name == null || name.isBlank()
                    || (worker.getName() != null
                    && worker.getName().toLowerCase()
                    .contains(name.trim().toLowerCase())))
            .filter(worker -> location == null || location.isBlank()
                    || (worker.getLocation() != null
                    && worker.getLocation().toLowerCase()
                    .contains(location.trim().toLowerCase())))
            .toList();

    model.addAttribute("workers", filteredWorkers);
    model.addAttribute("workerCount", allWorkers.size());
    model.addAttribute("searchName", name == null ? "" : name);
    model.addAttribute("searchLocation", location == null ? "" : location);

    return "admin-workers";
}
    // Booking Management
    @GetMapping("/admin/bookings")
    public String showBookings(Model model) {

        model.addAttribute("bookings", Collections.emptyList());
        model.addAttribute("bookingCount", 0);

        
        return "admin-bookings";
    }

    // Cooperative Management
 // Cooperative Management
@GetMapping("/admin/cooperatives")
public String showCooperatives(Model model) {

    List<Cooperative> cooperatives = cooperativeService.findAll();

    model.addAttribute("cooperatives", cooperatives);
    model.addAttribute("cooperativeCount", cooperatives.size());

    if (!model.containsAttribute("cooperativeForm")) {
        model.addAttribute(
                "cooperativeForm",
                new CreateCooperativeRequest("", "", "", "", "")
        );
    }

    return "admin-cooperatives";
}

@PostMapping("/admin/cooperatives")
public String createCooperative(
        @Valid @org.springframework.web.bind.annotation.ModelAttribute(
                "cooperativeForm"
        ) CreateCooperativeRequest request,
        BindingResult bindingResult,
        Model model,
        RedirectAttributes redirectAttributes) {

    if (bindingResult.hasErrors()) {
        List<Cooperative> cooperatives = cooperativeService.findAll();

        model.addAttribute("cooperatives", cooperatives);
        model.addAttribute("cooperativeCount", cooperatives.size());

        return "admin-cooperatives";
    }

    try {
        cooperativeService.create(request);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Cooperative created successfully."
        );

        return "redirect:/admin/cooperatives";

    } catch (ResponseStatusException ex) {

        if (ex.getStatusCode() == HttpStatus.CONFLICT) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "A cooperative with this name already exists."
            );

            return "redirect:/admin/cooperatives";
        }

        throw ex;
    }
}

@PostMapping("/admin/cooperatives/{id}/delete")
public String deleteCooperative(
        @PathVariable Long id,
        RedirectAttributes redirectAttributes) {

    try {
        cooperativeService.deleteById(id);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Cooperative deleted successfully."
        );

    } catch (ResponseStatusException ex) {

        if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "The cooperative was not found."
            );
        } else {
            throw ex;
        }
    }

    return "redirect:/admin/cooperatives";
}
}