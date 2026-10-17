
package com.servicenexus.repository;

import com.servicenexus.model.WorkerAvailability;
import com.servicenexus.model.WorkerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkerAvailabilityRepository
        extends JpaRepository<WorkerAvailability, Long> {

    List<WorkerAvailability> findByWorkerProfileOrderByDayOfWeekAsc(
            WorkerProfile workerProfile);
}
