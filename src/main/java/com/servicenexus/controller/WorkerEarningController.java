
package com.servicenexus.controller;

import com.servicenexus.model.WorkerProfile;
import com.servicenexus.repository.WorkerEarningRepository;
import com.servicenexus.repository.WorkerProfileRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;

@Controller
public class WorkerEarningController {

    private final WorkerEarningRepository earningRepository;
    private final WorkerProfileRepository profileRepository;

    public WorkerEarningController(
            WorkerEarningRepository earningRepository,
            WorkerProfileRepository profileRepository) {
        this.earningRepository = earningRepository;
        this.profileRepository = profileRepository;
    }

    @GetMapping("/worker/earnings")
    public String showEarnings(Model model) {

        WorkerProfile profile = profileRepository.findAll()
                .stream()
                .filter(p -> p.getId() != null)
                .max(Comparator.comparing(WorkerProfile::getId))
                .orElse(null);

        if (profile == null) {
            model.addAttribute("errorMessage",
                    "Please create a worker profile first.");
            model.addAttribute("earningsHistory", Collections.emptyList());
            model.addAttribute("totalEarnings", BigDecimal.ZERO);
            model.addAttribute("monthlyEarnings", BigDecimal.ZERO);
            model.addAttribute("completedJobCount", 0);
            return "worker-earnings";
        }

        Long workerId = profile.getId();

        LocalDate today = LocalDate.now();
        LocalDateTime monthStart = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime nextMonthStart =
                today.plusMonths(1).withDayOfMonth(1).atStartOfDay();

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
        model.addAttribute("completedJobCount", earningsHistory.size());

        return "worker-earnings";
    }
}
