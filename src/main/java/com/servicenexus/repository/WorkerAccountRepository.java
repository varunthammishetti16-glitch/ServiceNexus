
package com.servicenexus.repository;

import com.servicenexus.entity.WorkerAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WorkerAccountRepository extends JpaRepository<WorkerAccount, Long> {

    Optional<WorkerAccount> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    @Query("SELECT wa FROM WorkerAccount wa LEFT JOIN FETCH wa.workerProfile")
    List<WorkerAccount> findAllWithWorkerProfile();

    @Query("SELECT wa FROM WorkerAccount wa JOIN FETCH wa.workerProfile WHERE LOWER(wa.email) = LOWER(:email)")
    Optional<WorkerAccount> findByEmailWithWorkerProfile(@Param("email") String email);
}
