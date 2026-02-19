package org.example.analytics.service;

import org.example.analytics.DTO.OverviewResponseDTO;
import org.example.analytics.DTO.TopUserDTO;
import org.example.analytics.DTO.TrendDTO;
import org.example.analytics.repository.ComplaintRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service

public class AnalyticsService {

    private final ComplaintRepo complaintRepository;

    public AnalyticsService(ComplaintRepo complaintRepository) {
        this.complaintRepository = complaintRepository;
    }

    private Map<String, Long> mapToKeyValue(List<Object[]> data) {
        return data.stream()
                .collect(Collectors.toMap(
                        row -> row[0].toString(),
                        row -> ((Number) row[1]).longValue()
                ));
    }

    public OverviewResponseDTO getOverview() {

        Long total = complaintRepository.count();

        Map<String, Long> statusMap =
                mapToKeyValue(complaintRepository.countGroupByStatus());

        Map<String, Long> categoryMap =
                mapToKeyValue(complaintRepository.countByCategoryGroup());

        Map<String, Long> blockMap =
                mapToKeyValue(complaintRepository.countByBlock());

        Double avgResolution =
                complaintRepository.averageResolutionTime();

        return OverviewResponseDTO.builder()
                .totalComplaints(total)
                .statusDistribution(statusMap)
                .categoryDistribution(categoryMap)
                .blockDistribution(blockMap)
                .averageResolutionTimeInHours(avgResolution)
                .build();
    }

    private List<TrendDTO> mapTrend(List<Object[]> data) {
        return data.stream()
                .map(row -> new TrendDTO(
                        row[0].toString(),
                        ((Number) row[1]).longValue()
                ))
                .toList();
    }

    public List<TrendDTO> getDailyTrend() {
        return mapTrend(complaintRepository.dailyTrend());
    }

    public List<TrendDTO> getWeeklyTrend() {
        return mapTrend(complaintRepository.weeklyTrend());
    }

    public List<TrendDTO> getMonthlyTrend() {
        return mapTrend(complaintRepository.monthlyTrend());
    }

    public List<TrendDTO> getPeakHours() {
        return mapTrend(complaintRepository.peakHours());
    }

    public List<TopUserDTO> getTopUsers() {

        return complaintRepository.findTopUsers()
                .stream()
                .map(row -> new TopUserDTO(
                        ((Number) row[0]).longValue(),
                        ((Number) row[1]).longValue()
                ))
                .toList();
    }

}
