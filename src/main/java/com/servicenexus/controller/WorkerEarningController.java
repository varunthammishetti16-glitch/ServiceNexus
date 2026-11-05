package com.servicenexus.controller;

import com.servicenexus.entity.WorkerAccount;
import com.servicenexus.model.WorkerProfile;
import com.servicenexus.repository.WorkerAccountRepository;
import com.servicenexus.repository.WorkerEarningRepository;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
public class WorkerEarningController {

    private final WorkerEarningRepository earningRepository;
    private final WorkerAccountRepository workerAccountRepository;

    public WorkerEarningController(
            WorkerEarningRepository earningRepository,
            WorkerAccountRepository workerAccountRepository) {

        this.earningRepository = earningRepository;
        this.workerAccountRepository = workerAccountRepository;
    }

    @GetMapping("/worker/earnings")
    public String showEarnings(
            Model model,
            Authentication authentication) {

        if (authentication == null) {
            return "redirect:/login";
        }

        WorkerAccount account = workerAccountRepository
                .findByEmailWithWorkerProfile(authentication.getName())
                .orElseThrow(() ->
                        new IllegalStateException("Worker account not found"));

        if (!"APPROVED".equalsIgnoreCase(
                account.getVerificationStatus())) {
            return "redirect:/login";
        }

        WorkerProfile profile = account.getWorkerProfile();
        Long workerId = profile.getId();

        LocalDate today = LocalDate.now();

        LocalDateTime monthStart =
                today.withDayOfMonth(1).atStartOfDay();

        LocalDateTime nextMonthStart =
                today.plusMonths(1)
                        .withDayOfMonth(1)
                        .atStartOfDay();

        var earningsHistory =
                earningRepository
                        .findByWorkerProfileIdOrderByEarnedAtDesc(workerId);

        model.addAttribute("workerProfile", profile);
        model.addAttribute("earningsHistory", earningsHistory);

        model.addAttribute(
                "totalEarnings",
                earningRepository.getTotalEarnings(workerId));

        model.addAttribute(
                "monthlyEarnings",
                earningRepository.getMonthlyEarnings(
                        workerId, monthStart, nextMonthStart));

        model.addAttribute(
                "completedJobCount",
                earningsHistory.size());

        return "worker-earnings";
    }
}