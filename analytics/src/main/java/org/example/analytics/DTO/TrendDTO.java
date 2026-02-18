package org.example.analytics.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@   AllArgsConstructor
public class TrendDTO {
    private String period;
    private Long count;
}