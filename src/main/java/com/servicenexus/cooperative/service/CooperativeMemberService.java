
package com.servicenexus.cooperative.service;

import com.servicenexus.cooperative.entity.Cooperative;
import com.servicenexus.cooperative.entity.CooperativeMember;
import com.servicenexus.cooperative.repository.CooperativeMemberRepository;
import com.servicenexus.cooperative.repository.CooperativeRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CooperativeMemberService {

    private final CooperativeMemberRepository memberRepository;
    private final CooperativeRepository cooperativeRepository;

    public CooperativeMemberService(
            CooperativeMemberRepository memberRepository,
            CooperativeRepository cooperativeRepository) {
        this.memberRepository = memberRepository;
        this.cooperativeRepository = cooperativeRepository;
    }

    @Transactional(readOnly = true)
    public List<CooperativeMember> findByCooperative(Long cooperativeId) {
        if (!cooperativeRepository.existsById(cooperativeId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Cooperative not found"
            );
        }

        return memberRepository.findByCooperative_Id(cooperativeId);
    }

    @Transactional
    public CooperativeMember addMember(
            Long cooperativeId,
            Long workerId,
            String role) {

        if (cooperativeId == null || cooperativeId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A valid cooperative ID is required"
            );
        }

        if (workerId == null || workerId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A valid worker ID is required"
            );
        }

        Cooperative cooperative = cooperativeRepository.findById(cooperativeId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Cooperative not found"
                ));

        if (memberRepository.existsByCooperative_IdAndWorkerId(
                cooperativeId, workerId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This worker is already a member of this cooperative"
            );
        }

        String normalizedRole = (role == null || role.isBlank())
                ? "MEMBER"
                : role.trim().toUpperCase();

        if (!normalizedRole.equals("MEMBER")
                && !normalizedRole.equals("COORDINATOR")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Role must be MEMBER or COORDINATOR"
            );
        }

        CooperativeMember member = new CooperativeMember(
                cooperative,
                workerId,
                normalizedRole
        );

        return memberRepository.save(member);
    }
}
