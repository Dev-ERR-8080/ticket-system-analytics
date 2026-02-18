package org.example.analytics.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TopUserDTO {
    private Long userId;
    private Long complaintCount;
}
