package com.servicenexus.controller;

import com.servicenexus.repository.CustomerRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final CustomerRepository customerRepository;

    public HomeController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GetMapping("/")
    public String home(Authentication authentication, Model model) {

        boolean customerLoggedIn = authentication != null
                && authentication.isAuthenticated()
                && authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_CUSTOMER"));

        model.addAttribute("customerLoggedIn", customerLoggedIn);

        if (customerLoggedIn) {
            customerRepository.findByEmailIgnoreCase(authentication.getName())
                    .ifPresent(customer -> {
                        model.addAttribute("customerName", customer.getName());
                        model.addAttribute("customerId", customer.getId());
                    });
        }

        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }
}
