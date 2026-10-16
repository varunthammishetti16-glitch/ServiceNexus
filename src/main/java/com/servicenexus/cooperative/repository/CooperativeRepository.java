
package com.servicenexus.cooperative.repository;

import com.servicenexus.cooperative.entity.Cooperative;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CooperativeRepository
        extends JpaRepository<Cooperative, Long> {

    boolean existsByNameIgnoreCase(String name);
}
