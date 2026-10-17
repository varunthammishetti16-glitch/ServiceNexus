
package com.servicenexus.repository;

import com.servicenexus.model.WorkerEarning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WorkerEarningRepository
        extends JpaRepository<WorkerEarning, Long> {

    List<WorkerEarning> findByWorkerProfileIdOrderByEarnedAtDesc(
            Long workerProfileId
    );

    boolean existsByBookingId(Long bookingId);

    Optional<WorkerEarning> findByBookingId(Long bookingId);

    @Query("""
        SELECT COALESCE(SUM(e.amount), 0)
        FROM WorkerEarning e
        WHERE e.workerProfileId = :workerProfileId
        """)
    BigDecimal getTotalEarnings(
            @Param("workerProfileId") Long workerProfileId
    );

    @Query("""
        SELECT COALESCE(SUM(e.amount), 0)
        FROM WorkerEarning e
        WHERE e.workerProfileId = :workerProfileId
          AND e.earnedAt >= :start
          AND e.earnedAt < :end
        """)
    BigDecimal getMonthlyEarnings(
            @Param("workerProfileId") Long workerProfileId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
