
package com.servicenexus.repository;

import com.servicenexus.model.WorkerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkerProfileRepository
        extends JpaRepository<WorkerProfile, Long> {
}
