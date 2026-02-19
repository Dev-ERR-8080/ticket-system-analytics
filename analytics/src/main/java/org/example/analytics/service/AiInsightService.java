package org.example.analytics.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.example.analytics.DTO.AiInsightRequestDTO;
import org.example.analytics.DTO.AiInsightResponseDTO;
import org.example.analytics.DTO.AnomalyDTO;
import org.example.analytics.repository.ComplaintRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiInsightService {

    private final ComplaintRepo  complaintRepo;
    private final AnomalyService anomalyService;
    private final ObjectMapper   objectMapper = new ObjectMapper();

    @Value("${ai.api.key}")
    private String apiKey;

    @Value("${ai.api.model:gemini-2.0-flash}")
    private String model;

    // ─── Main entry: route by scenario ───────────────────────────────────────

    public AiInsightResponseDTO getInsight(AiInsightRequestDTO request) {
        String prompt      = buildPrompt(request);
        String explanation = callGemini(prompt);
        return AiInsightResponseDTO.builder()
                .scenario(request.getScenario())
                .explanation(explanation)
                .build();
    }

    // ─── Auto-insight: summarise all current anomalies ────────────────────────

    public AiInsightResponseDTO getAnomalySummary() {
        List<AnomalyDTO> anomalies = anomalyService.detectAll();

        if (anomalies.isEmpty()) {
            return AiInsightResponseDTO.builder()
                    .scenario("ANOMALY_SUMMARY")
                    .explanation("No anomalies detected in the current dataset.")
                    .build();
        }

        String anomalyJson;
        try {
            anomalyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(anomalies);
        } catch (Exception e) {
            anomalyJson = anomalies.toString();
        }

        String prompt = """
                You are an analytics assistant for a complaint/ticket management system.
                The following anomalies have been detected automatically:

                %s

                Please provide a concise, management-level summary explaining:
                1. What the most critical issues are.
                2. Possible root causes for each anomaly type.
                3. Recommended actions for the operations team.
                Keep the response clear and under 300 words.
                """.formatted(anomalyJson);

        String explanation = callGemini(prompt);
        return AiInsightResponseDTO.builder()
                .scenario("ANOMALY_SUMMARY")
                .explanation(explanation)
                .build();
    }

    // ─── Prompt builder per scenario ─────────────────────────────────────────

    private String buildPrompt(AiInsightRequestDTO req) {
        return switch (req.getScenario().toUpperCase()) {

            case "USER_BEHAVIOR" -> {
                String userId = req.getContext();
                long   count  = complaintRepo.countByRaisedBy(parseLong(userId));
                List<Object[]> topUsers = complaintRepo.findTopUsers();
                double mean = topUsers.stream()
                        .mapToLong(r -> ((Number) r[1]).longValue())
                        .average().orElse(0);

                yield """
                        You are an analytics assistant. A particular user (ID: %s) has raised %d tickets,
                        while the average number of tickets per user is %.1f.

                        Explain in 2-3 paragraphs why this user might be raising so many tickets.
                        Consider possibilities such as: repeated unresolved issues, system bugs affecting
                        a specific area, misuse of the ticketing system, or a role that naturally
                        generates more requests. Suggest what the management should investigate.
                        """.formatted(userId, count, mean);
            }

            case "TICKET_SPIKE" -> {
                List<Object[]> daily = complaintRepo.dailyTrend();
                String trendSummary  = formatRows(daily);
                yield """
                        You are an analytics assistant. Below is the daily ticket volume trend:

                        %s

                        A recent spike has been detected. In 2-3 paragraphs, explain possible reasons
                        why the ticket count increased sharply. Consider system deployments, seasonal
                        demand, a newly reported bug, or an external event. Recommend actions.
                        """.formatted(trendSummary);
            }

            case "RESOLUTION_TIME" -> {
                Double avg = complaintRepo.averageResolutionTime();
                yield """
                        You are an analytics assistant. The average ticket resolution time is
                        currently %.1f hours.

                        In 2-3 paragraphs, explain why resolution time might be increasing.
                        Consider factors like staff capacity, ticket complexity, lack of escalation
                        policies, or tool inefficiencies. Recommend what management can do to
                        reduce resolution time.
                        """.formatted(avg != null ? avg : 0.0);
            }

            case "GENERAL" -> {
                long           total    = complaintRepo.count();
                List<Object[]> status   = complaintRepo.countGroupByStatus();
                List<Object[]> category = complaintRepo.countByCategoryGroup();
                yield """
                        You are an analytics assistant. Here is a summary of the ticketing system:

                        Total tickets      : %d
                        Status breakdown   : %s
                        Category breakdown : %s
                        Additional context : %s

                        Provide a management-level analysis in 3 paragraphs covering system health,
                        workload distribution, and any concerns you notice.
                        """.formatted(total, formatRows(status), formatRows(category),
                        req.getContext() != null ? req.getContext() : "none");
            }

            default -> "Provide a brief general overview of a ticket management analytics system.";
        };
    }

    // ─── Gemini API call ──────────────────────────────────────────────────────

    private String callGemini(String prompt) {
        try {
            // Build request body properly using Jackson nodes
            ObjectNode body = objectMapper.createObjectNode();

            ArrayNode contents = objectMapper.createArrayNode();
            ObjectNode contentItem = objectMapper.createObjectNode();
            ArrayNode parts = objectMapper.createArrayNode();
            ObjectNode part = objectMapper.createObjectNode();

            part.put("text", prompt);
            parts.add(part);
            contentItem.set("parts", parts);
            contents.add(contentItem);
            body.set("contents", contents);

            ObjectNode generationConfig = objectMapper.createObjectNode();
            generationConfig.put("maxOutputTokens", 600);
            generationConfig.put("temperature", 0.4);
            body.set("generationConfig", generationConfig);

            String jsonBody = objectMapper.writeValueAsString(body);

            String url = "https://generativelanguage.googleapis.com/v1beta/models/"
                    + model + ":generateContent?key=" + apiKey;

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(httpRequest, HttpResponse.BodyHandlers.ofString());

            JsonNode root = objectMapper.readTree(response.body());

            if (response.statusCode() != 200) {
                return "Gemini API error (" + response.statusCode() + "): "
                        + root.path("error").path("message").asText("Unknown error");
            }

            return root.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText("No response from Gemini.");

        } catch (Exception e) {
            return "Failed to reach Gemini API: " + e.getMessage();
        }
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private String formatRows(List<Object[]> rows) {
        StringBuilder sb = new StringBuilder();
        for (Object[] row : rows) {
            sb.append(row[0]).append(": ").append(row[1]).append("\n");
        }
        return sb.toString();
    }

    private long parseLong(String s) {
        try { return Long.parseLong(s); } catch (Exception e) { return 0L; }
    }
}