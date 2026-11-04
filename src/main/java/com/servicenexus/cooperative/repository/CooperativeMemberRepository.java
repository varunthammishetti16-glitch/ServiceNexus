
package com.servicenexus.cooperative.repository;

import com.servicenexus.cooperative.entity.CooperativeMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CooperativeMemberRepository
        extends JpaRepository<CooperativeMember, Long> {

    List<CooperativeMember> findByCooperative_Id(Long cooperativeId);

    boolean existsByCooperative_IdAndWorkerId(
            Long cooperativeId,
            Long workerId
    );
}
