
package com.servicenexus.controller;

import com.servicenexus.entity.WorkerAccount;
import com.servicenexus.repository.WorkerAccountRepository;
import com.servicenexus.repository.WorkerProfileRepository;

import com.servicenexus.cooperative.dto.CreateCooperativeRequest;
import com.servicenexus.cooperative.entity.Cooperative;
import com.servicenexus.cooperative.service.CooperativeService;

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

import jakarta.validation.Valid;

@Controller
public class AdminController {

    private final WorkerProfileRepository workerProfileRepository;
    private final WorkerAccountRepository workerAccountRepository;
    private final CooperativeService cooperativeService;

    public AdminController(
            CooperativeService cooperativeService,
            WorkerProfileRepository workerProfileRepository,
            WorkerAccountRepository workerAccountRepository) {

        this.cooperativeService = cooperativeService;
        this.workerProfileRepository = workerProfileRepository;
        this.workerAccountRepository = workerAccountRepository;
    }

    // ==============================
    // ADMIN DASHBOARD
    // ==============================

    @GetMapping("/admin/dashboard")
    public String showDashboard(Model model) {

        model.addAttribute("customerCount", 0);

        model.addAttribute(
                "workerCount",
                workerAccountRepository.count()
        );

        model.addAttribute("bookingCount", 0);

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

    // ==============================
    // CUSTOMER MANAGEMENT
    // ==============================

    @GetMapping("/admin/customers")
    public String showCustomers(Model model) {

        model.addAttribute("customers", Collections.emptyList());
        model.addAttribute("customerCount", 0);

        return "admin-customers";
    }

    // ==============================
    // WORKER MANAGEMENT
    // ==============================

    @GetMapping("/admin/workers")
    public String showWorkers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String location,
            Model model) {

        List<WorkerAccount> allWorkers =
                workerAccountRepository.findAllWithWorkerProfile();

        List<WorkerAccount> filteredWorkers = allWorkers.stream()

                .filter(account -> account.getWorkerProfile() != null)

                .filter(account -> name == null || name.isBlank()
                        || (account.getWorkerProfile().getName() != null
                        && account.getWorkerProfile().getName()
                        .toLowerCase()
                        .contains(name.trim().toLowerCase()))
                        || (account.getWorkerProfile().getSkills() != null
                        && account.getWorkerProfile().getSkills()
                        .toLowerCase()
                        .contains(name.trim().toLowerCase())))

                .filter(account -> location == null || location.isBlank()
                        || (account.getWorkerProfile().getLocation() != null
                        && account.getWorkerProfile().getLocation()
                        .toLowerCase()
                        .contains(location.trim().toLowerCase())))

                .toList();

        model.addAttribute("workers", filteredWorkers);
        model.addAttribute("workerCount", allWorkers.size());
        model.addAttribute("searchName", name == null ? "" : name);
        model.addAttribute("searchLocation",
                location == null ? "" : location);

        return "admin-workers";
    }

    // APPROVE WORKER

    @PostMapping("/admin/workers/{id}/approve")
    public String approveWorker(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        WorkerAccount worker = workerAccountRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Worker not found"));

        worker.setVerificationStatus("APPROVED");

        workerAccountRepository.save(worker);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Worker approved successfully."
        );

        return "redirect:/admin/workers";
    }

    // REJECT WORKER

    @PostMapping("/admin/workers/{id}/reject")
    public String rejectWorker(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        WorkerAccount worker = workerAccountRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Worker not found"));

        worker.setVerificationStatus("REJECTED");

        workerAccountRepository.save(worker);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Worker rejected successfully."
        );

        return "redirect:/admin/workers";
    }

    // ==============================
    // BOOKING MANAGEMENT
    // ==============================

    @GetMapping("/admin/bookings")
    public String showBookings(Model model) {

        model.addAttribute("bookings", Collections.emptyList());
        model.addAttribute("bookingCount", 0);

        return "admin-bookings";
    }

    // ==============================
    // COOPERATIVE MANAGEMENT
    // ==============================

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

            List<Cooperative> cooperatives =
                    cooperativeService.findAll();

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
