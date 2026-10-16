
package com.servicenexus.cooperative.service;

import com.servicenexus.cooperative.dto.CreateCooperativeRequest;
import com.servicenexus.cooperative.entity.Cooperative;
import com.servicenexus.cooperative.repository.CooperativeRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CooperativeService {

    private final CooperativeRepository repository;

    public CooperativeService(CooperativeRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Cooperative> findAll() {
        return repository.findAll();
    }

    @Transactional
    public Cooperative create(CreateCooperativeRequest request) {
        String name = request.name().trim();

        if (repository.existsByNameIgnoreCase(name)) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "A cooperative with this name already exists"
            );
        }

        Cooperative cooperative = new Cooperative(
            name,
            request.description(),
            request.location().trim(),
            request.servicesOffered(),
            request.contactEmail()
        );

        try {
            return repository.saveAndFlush(cooperative);
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "A cooperative with this name already exists"
            );
        }
    }
}
