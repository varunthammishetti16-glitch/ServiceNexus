
package com.servicenexus.repository;

import com.servicenexus.model.WorkerService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkerServiceRepository
        extends JpaRepository<WorkerService, Long> {

    List<WorkerService> findByActiveTrue();

    List<WorkerService> findByActiveTrueAndNameContainingIgnoreCase(
            String name);

    List<WorkerService> findByActiveTrueAndCategoryContainingIgnoreCase(
            String category);

    List<WorkerService> findByActiveTrueAndLocationContainingIgnoreCase(
            String location);
}
