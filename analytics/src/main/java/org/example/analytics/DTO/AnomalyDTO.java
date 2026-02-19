package org.example.analytics.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnomalyDTO {
    private String type;
    private String severity;
    private String description;
    private Object details;
}