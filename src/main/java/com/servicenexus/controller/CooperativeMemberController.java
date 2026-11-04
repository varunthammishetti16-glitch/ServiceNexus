
package com.servicenexus.controller;

import com.servicenexus.cooperative.entity.CooperativeMember;
import com.servicenexus.cooperative.service.CooperativeMemberService;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/cooperative-members")
public class CooperativeMemberController {

    private final CooperativeMemberService memberService;

    public CooperativeMemberController(
            CooperativeMemberService memberService) {
        this.memberService = memberService;
    }

    public record AddMemberRequest(
            @NotNull @Positive Long cooperativeId,
            @NotNull @Positive Long workerId,
            String role) {
    }

    public record MemberResponse(
            Long id,
            Long cooperativeId,
            Long workerId,
            String role,
            LocalDateTime joinedAt) {
    }

    @GetMapping
    public List<MemberResponse> getMembers(
            @RequestParam @Positive Long cooperativeId) {

        return memberService.findByCooperative(cooperativeId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MemberResponse addMember(
            @jakarta.validation.Valid @RequestBody AddMemberRequest request) {

        CooperativeMember member = memberService.addMember(
                request.cooperativeId(),
                request.workerId(),
                request.role());

        return toResponse(member);
    }

    private MemberResponse toResponse(CooperativeMember member) {
        return new MemberResponse(
                member.getId(),
                member.getCooperative().getId(),
                member.getWorkerId(),
                member.getRole(),
                member.getJoinedAt());
    }
}
