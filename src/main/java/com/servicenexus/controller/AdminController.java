package com.servicenexus.controller;

import java.util.Collections;
import java.util.List;

import com.servicenexus.cooperative.entity.Cooperative;
import com.servicenexus.cooperative.service.CooperativeService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    private final CooperativeService cooperativeService;

    public AdminController(CooperativeService cooperativeService) {
        this.cooperativeService = cooperativeService;
    }

    // Admin Dashboard
    @GetMapping("/admin/dashboard")
    public String showDashboard(Model model) {

        model.addAttribute("customerCount", 0);
        model.addAttribute("workerCount", 0);
        model.addAttribute("bookingCount", 0);
        model.addAttribute("cooperativeCount",
                cooperativeService.findAll().size());

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
    public String showWorkers(Model model) {

        model.addAttribute("workers", Collections.emptyList());
        model.addAttribute("workerCount", 0);

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
    @GetMapping("/admin/cooperatives")
    public String showCooperatives(Model model) {

        List<Cooperative> cooperatives =
                cooperativeService.findAll();

        model.addAttribute("cooperatives", cooperatives);
        model.addAttribute("cooperativeCount", cooperatives.size());

        return "admin-cooperatives";
    }
}