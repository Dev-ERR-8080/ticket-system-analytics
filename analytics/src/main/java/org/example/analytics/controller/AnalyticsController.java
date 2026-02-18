package org.example.analytics.controller;

import lombok.RequiredArgsConstructor;
import org.example.analytics.DTO.OverviewResponseDTO;
import org.example.analytics.DTO.TopUserDTO;
import org.example.analytics.DTO.TrendDTO;
import org.example.analytics.service.AnalyticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/overview")
    public OverviewResponseDTO overview() {
        return analyticsService.getOverview();
    }

    @GetMapping("/trends/daily")
    public List<TrendDTO> dailyTrend() {
        return analyticsService.getDailyTrend();
    }

    @GetMapping("/trends/weekly")
    public List<TrendDTO> weeklyTrend() {
        return analyticsService.getWeeklyTrend();
    }

    @GetMapping("/trends/monthly")
    public List<TrendDTO> monthlyTrend() {
        return analyticsService.getMonthlyTrend();
    }

    @GetMapping("/peak-hours")
    public List<TrendDTO> peakHours() {
        return analyticsService.getPeakHours();
    }

    @GetMapping("/top-users")
    public List<TopUserDTO> topUsers() {
        return analyticsService.getTopUsers();
    }
}
