package com.servicenexus.controller;

import com.servicenexus.model.WorkerService;
import com.servicenexus.repository.WorkerServiceRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Stream;

@Controller
public class ServiceController {

    private final WorkerServiceRepository workerServiceRepository;

    public ServiceController(
            WorkerServiceRepository workerServiceRepository) {
        this.workerServiceRepository = workerServiceRepository;
    }

    @GetMapping("/services")
    public String searchServices(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String location,
            Model model) {

        List<WorkerService> services =
                workerServiceRepository.findByActiveTrue();

        Stream<WorkerService> filtered = services.stream();

        if (q != null && !q.isBlank()) {
            String query = q.trim().toLowerCase();

            filtered = filtered.filter(service ->
            (service.getName() != null
                    && service.getName().toLowerCase().contains(query))
            || (service.getCategory() != null
                    && service.getCategory().toLowerCase().contains(query))
            || (service.getDescription() != null
                    && service.getDescription().toLowerCase().contains(query))
    );
}

        if (location != null && !location.isBlank()) {
    String searchLocation = location.trim().toLowerCase();

    filtered = filtered.filter(service ->
            service.getLocation() != null
                    && service.getLocation().toLowerCase()
                            .contains(searchLocation)
    );
}

        model.addAttribute("query", q);
        model.addAttribute("location", location);
        model.addAttribute("services", filtered.toList());

        return "services";
    }
}