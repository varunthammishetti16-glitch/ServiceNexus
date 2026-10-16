package com.servicenexus.controller;

import java.util.Collections;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @GetMapping("/admin/dashboard")
    public String showDashboard(Model model) {

        model.addAttribute("customerCount", 0);
        model.addAttribute("workerCount", 0);
        model.addAttribute("bookingCount", 0);
        model.addAttribute("cooperativeCount", 0);

        return "admin-dashboard";
    }

    @GetMapping("/admin/customers")
    public String showCustomers(Model model) {

        model.addAttribute("customers", Collections.emptyList());
        model.addAttribute("customerCount", 0);

        return "admin-customers";
    }

    @GetMapping("/admin/workers")
public String showWorkers(Model model) {

    model.addAttribute("workers", Collections.emptyList());
    model.addAttribute("workerCount", 0);

    return "admin-workers";
}
@GetMapping("/admin/bookings")
public String showBookings(Model model) {

    model.addAttribute("bookings", Collections.emptyList());
    model.addAttribute("bookingCount", 0);

    return "admin-bookings";
}
@GetMapping("/admin/cooperatives")
public String showCooperatives(Model model) {

    model.addAttribute("cooperatives", Collections.emptyList());
    model.addAttribute("cooperativeCount", 0);

    return "admin-cooperatives";
}
}