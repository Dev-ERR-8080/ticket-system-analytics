package org.example.analytics.service;

import lombok.RequiredArgsConstructor;
import org.example.analytics.DTO.AnomalyDTO;
import org.example.analytics.model.Complaint;
import org.example.analytics.model.Status;
import org.example.analytics.repository.ComplaintRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnomalyService {

    private final ComplaintRepo complaintRepo;

    // ─── Configurable thresholds ──────────────────────────────────────────────
    private static final int    HIGH_USER_TICKET_THRESHOLD   = 10;   // tickets per user
    private static final double SPIKE_ZSCORE_THRESHOLD       = 2.0;  // z-score for daily spike
    private static final double SIMILARITY_RATIO_THRESHOLD   = 0.6;  // 60 % similar words = duplicate
    private static final double SLOW_RESOLUTION_MULTIPLIER   = 2.0;  // 2× average = slow
    private static final long   SLA_HOURS                    = 48;   // SLA window in hours

    // ─── Entry point ─────────────────────────────────────────────────────────

    public List<AnomalyDTO> detectAll() {
        List<AnomalyDTO> anomalies = new ArrayList<>();
        anomalies.addAll(detectHighVolumeUsers());
        anomalies.addAll(detectDailySpike());
        anomalies.addAll(detectDuplicateTickets());
        anomalies.addAll(detectSlowResolution());
        anomalies.addAll(detectSlaViolations());
        return anomalies;
    }

    // ─── 1. User raising unusually high number of tickets ────────────────────

    public List<AnomalyDTO> detectHighVolumeUsers() {
        List<Object[]> rows = complaintRepo.findTopUsers();
        if (rows.isEmpty()) return Collections.emptyList();

        // Compute mean and std-dev across all user counts
        List<Long> counts = rows.stream()
                .map(r -> ((Number) r[1]).longValue())
                .toList();

        double mean  = counts.stream().mapToLong(Long::longValue).average().orElse(0);
        double std   = stdDev(counts, mean);

        List<AnomalyDTO> anomalies = new ArrayList<>();
        for (Object[] row : rows) {
            Long userId = ((Number) row[0]).longValue();
            long count  = ((Number) row[1]).longValue();

            boolean overThreshold = count >= HIGH_USER_TICKET_THRESHOLD;
            boolean highZScore    = std > 0 && zScore(count, mean, std) >= SPIKE_ZSCORE_THRESHOLD;

            if (overThreshold || highZScore) {
                anomalies.add(AnomalyDTO.builder()
                        .type("HIGH_USER_VOLUME")
                        .severity(count >= HIGH_USER_TICKET_THRESHOLD * 2 ? "HIGH" : "MEDIUM")
                        .description("User " + userId + " has raised " + count + " tickets (mean=" +
                                String.format("%.1f", mean) + ", z=" +
                                String.format("%.2f", std > 0 ? zScore(count, mean, std) : 0) + ")")
                        .details(Map.of("userId", userId, "count", count,
                                "mean", String.format("%.1f", mean)))
                        .build());
            }
        }
        return anomalies;
    }

    // ─── 2. Sudden spike in daily ticket volume ───────────────────────────────

    public List<AnomalyDTO> detectDailySpike() {
        List<Object[]> rows = complaintRepo.dailyTrend();
        if (rows.size() < 3) return Collections.emptyList();

        List<Long> counts = rows.stream()
                .map(r -> ((Number) r[1]).longValue())
                .toList();

        double mean = counts.stream().mapToLong(Long::longValue).average().orElse(0);
        double std  = stdDev(counts, mean);

        List<AnomalyDTO> anomalies = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            long count = counts.get(i);
            if (std > 0 && zScore(count, mean, std) >= SPIKE_ZSCORE_THRESHOLD) {
                String day = rows.get(i)[0].toString();
                anomalies.add(AnomalyDTO.builder()
                        .type("DAILY_SPIKE")
                        .severity("HIGH")
                        .description("Ticket spike detected on " + day + ": " + count +
                                " tickets (z=" + String.format("%.2f", zScore(count, mean, std)) + ")")
                        .details(Map.of("date", day, "count", count,
                                "mean", String.format("%.1f", mean),
                                "stdDev", String.format("%.1f", std)))
                        .build());
            }
        }
        return anomalies;
    }

    // ─── 3. Repeated / duplicate tickets ─────────────────────────────────────

    public List<AnomalyDTO> detectDuplicateTickets() {
        List<Complaint> all = complaintRepo.findAll();
        List<AnomalyDTO> anomalies = new ArrayList<>();

        // Group by category first to limit comparisons
        Map<String, List<Complaint>> byCategory = all.stream()
                .collect(Collectors.groupingBy(c -> c.getCategory().name()));

        for (Map.Entry<String, List<Complaint>> entry : byCategory.entrySet()) {
            List<Complaint> group = entry.getValue();
            Set<Long> flagged = new HashSet<>();

            for (int i = 0; i < group.size(); i++) {
                for (int j = i + 1; j < group.size(); j++) {
                    Complaint a = group.get(i);
                    Complaint b = group.get(j);
                    if (flagged.contains(a.getId()) && flagged.contains(b.getId())) continue;

                    double sim = jaccardSimilarity(a.getDescription(), b.getDescription());
                    if (sim >= SIMILARITY_RATIO_THRESHOLD) {
                        flagged.add(a.getId());
                        flagged.add(b.getId());
                        anomalies.add(AnomalyDTO.builder()
                                .type("DUPLICATE_TICKET")
                                .severity("MEDIUM")
                                .description("Tickets #" + a.getId() + " and #" + b.getId() +
                                        " in category " + entry.getKey() +
                                        " appear similar (similarity=" +
                                        String.format("%.0f%%", sim * 100) + ")")
                                .details(Map.of("ticketIds", List.of(a.getId(), b.getId()),
                                        "category", entry.getKey(),
                                        "similarity", String.format("%.2f", sim)))
                                .build());
                    }
                }
            }
        }
        return anomalies;
    }

    // ─── 4. Extremely long resolution time ───────────────────────────────────

    public List<AnomalyDTO> detectSlowResolution() {
        Double avgHours = complaintRepo.averageResolutionTime();
        if (avgHours == null || avgHours == 0) return Collections.emptyList();

        double threshold = avgHours * SLOW_RESOLUTION_MULTIPLIER;
        LocalDateTime cutoff = LocalDateTime.now().minusHours((long) threshold);

        // Complaints still not resolved and created before the cutoff
        List<Complaint> violations = complaintRepo.findSlaViolations(cutoff);

        List<AnomalyDTO> anomalies = new ArrayList<>();
        for (Complaint c : violations) {
            long hoursOpen = java.time.Duration.between(c.getCreatedAt(), LocalDateTime.now()).toHours();
            anomalies.add(AnomalyDTO.builder()
                    .type("SLOW_RESOLUTION")
                    .severity(hoursOpen > threshold * 2 ? "HIGH" : "MEDIUM")
                    .description("Ticket #" + c.getId() + " has been open for " + hoursOpen +
                            "h (avg=" + String.format("%.1f", avgHours) + "h, threshold=" +
                            String.format("%.1f", threshold) + "h)")
                    .details(Map.of("ticketId", c.getId(),
                            "hoursOpen", hoursOpen,
                            "status", c.getStatus().name(),
                            "category", c.getCategory().name()))
                    .build());
        }
        return anomalies;
    }

    // ─── 5. SLA violations (open tickets older than SLA_HOURS) ───────────────

    public List<AnomalyDTO> detectSlaViolations() {
        LocalDateTime threshold = LocalDateTime.now().minusHours(SLA_HOURS);
        List<Complaint> violations = complaintRepo.findSlaViolations(threshold);

        return violations.stream().map(c -> {
            long hoursOpen = java.time.Duration.between(c.getCreatedAt(), LocalDateTime.now()).toHours();
            return AnomalyDTO.builder()
                    .type("SLA_VIOLATION")
                    .severity("HIGH")
                    .description("Ticket #" + c.getId() + " breached " + SLA_HOURS + "h SLA (" +
                            hoursOpen + "h open)")
                    .details(Map.of("ticketId", c.getId(),
                            "hoursOpen", hoursOpen,
                            "raisedBy", c.getRaisedBy(),
                            "category", c.getCategory().name()))
                    .build();
        }).toList();
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private double zScore(long value, double mean, double std) {
        return (value - mean) / std;
    }

    private double stdDev(List<Long> values, double mean) {
        double variance = values.stream()
                .mapToDouble(v -> Math.pow(v - mean, 2))
                .average().orElse(0);
        return Math.sqrt(variance);
    }

    /** Word-level Jaccard similarity between two texts */
    private double jaccardSimilarity(String a, String b) {
        if (a == null || b == null) return 0;
        Set<String> wordsA = new HashSet<>(Arrays.asList(a.toLowerCase().split("\\W+")));
        Set<String> wordsB = new HashSet<>(Arrays.asList(b.toLowerCase().split("\\W+")));
        Set<String> intersection = new HashSet<>(wordsA);
        intersection.retainAll(wordsB);
        Set<String> union = new HashSet<>(wordsA);
        union.addAll(wordsB);
        return union.isEmpty() ? 0 : (double) intersection.size() / union.size();
    }
}