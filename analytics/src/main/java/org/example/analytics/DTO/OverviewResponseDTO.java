package org.example.analytics.DTO;


import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class OverviewResponseDTO {

    private Long totalComplaints;

    private Map<String, Long> statusDistribution;

    private Map<String, Long> categoryDistribution;

    private Map<String, Long> blockDistribution;

    private Double averageResolutionTimeInHours;
}