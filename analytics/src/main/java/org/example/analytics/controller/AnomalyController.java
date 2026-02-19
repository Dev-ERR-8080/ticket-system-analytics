package org.example.analytics.controller;

import lombok.RequiredArgsConstructor;
import org.example.analytics.DTO.AnomalyDTO;
import org.example.analytics.service.AnomalyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/analytics/anomalies")
@RequiredArgsConstructor
public class AnomalyController {

    private final AnomalyService anomalyService;
    @GetMapping
    public List<AnomalyDTO> all() {
        return anomalyService.detectAll();
    }

    @GetMapping("/high-volume-users")
    public List<AnomalyDTO> highVolumeUsers() {
        return anomalyService.detectHighVolumeUsers();
    }

    @GetMapping("/daily-spike")
    public List<AnomalyDTO> dailySpike() {
        return anomalyService.detectDailySpike();
    }

    @GetMapping("/duplicates")
    public List<AnomalyDTO> duplicates() {
        return anomalyService.detectDuplicateTickets();
    }

    @GetMapping("/slow-resolution")
    public List<AnomalyDTO> slowResolution() {
        return anomalyService.detectSlowResolution();
    }

    @GetMapping("/sla-violations")
    public List<AnomalyDTO> slaViolations() {
        return anomalyService.detectSlaViolations();
    }
}