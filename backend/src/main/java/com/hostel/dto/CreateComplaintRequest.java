package com.hostel.dto;

import com.hostel.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateComplaintRequest {
    private String title;
    private String description;
    private Category category;
    private Long userId;
}
