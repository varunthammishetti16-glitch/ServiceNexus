
package com.servicenexus.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CooperativePageController {

    @GetMapping("/cooperatives")
    public String cooperativesPage() {
        return "cooperatives";
    }
    
@GetMapping("/cooperative-dashboard")
public String cooperativeDashboardPage() {
    return "cooperative-dashboard";
}
@GetMapping("/cooperative-members")
public String cooperativeMembersPage() {
    return "cooperative-members";
}

@GetMapping("/community-bookings")
public String communityBookingsPage() {
    return "community-bookings";
}

@GetMapping("/work-allocation")
public String workAllocationPage() {
    return "work-allocation";
}



}
