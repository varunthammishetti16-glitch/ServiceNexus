
package com.servicenexus.controller;

import com.servicenexus.cooperative.dto.CreateCooperativeRequest;
import com.servicenexus.cooperative.entity.Cooperative;
import com.servicenexus.cooperative.service.CooperativeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cooperatives")
public class CooperativeController {

    private final CooperativeService service;

    public CooperativeController(CooperativeService service) {
        this.service = service;
    }

    @GetMapping
    public List<Cooperative> getAll() {
        return service.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cooperative create(
            @Valid @RequestBody CreateCooperativeRequest request) {
        return service.create(request);
    }
}
