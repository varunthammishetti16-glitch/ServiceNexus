package com.servicenexus.repository;

import com.servicenexus.model.WorkerService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkerServiceRepository
        extends JpaRepository<WorkerService, Long> {

    // Get only active services for customer booking
    List<WorkerService> findByActiveTrueOrderByNameAsc();
}