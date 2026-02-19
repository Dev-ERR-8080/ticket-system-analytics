package org.example.analytics.controller;

import lombok.RequiredArgsConstructor;
import org.example.analytics.DTO.AiInsightRequestDTO;
import org.example.analytics.DTO.AiInsightResponseDTO;
import org.example.analytics.service.AiInsightService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/analytics/ai")
@RequiredArgsConstructor
public class AiInsightController {

    private final AiInsightService aiInsightService;

    @PostMapping("/insight")
    public AiInsightResponseDTO insight(@RequestBody AiInsightRequestDTO request) {
        return aiInsightService.getInsight(request);
    }

    /**
     * Auto-runs all anomaly detectors and asks the AI to summarise them.
     */
    @GetMapping("/anomaly-summary")
    public AiInsightResponseDTO anomalySummary() {
        return aiInsightService.getAnomalySummary();
    }
}