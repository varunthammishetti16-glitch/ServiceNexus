
package com.servicenexus.repository;

import com.servicenexus.model.WorkerService;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkerServiceRepository
        extends JpaRepository<WorkerService, Long> {
}
