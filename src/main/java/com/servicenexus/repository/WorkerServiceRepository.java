package com.servicenexus.repository;

import com.servicenexus.model.WorkerService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkerServiceRepository
        extends JpaRepository<WorkerService, Long> {

    List<WorkerService> findByActiveTrueOrderByNameAsc();

    List<WorkerService> findByWorkerProfileId(Long workerProfileId);

    Optional<WorkerService> findByIdAndWorkerProfileId(
            Long id,
            Long workerProfileId
    );
}