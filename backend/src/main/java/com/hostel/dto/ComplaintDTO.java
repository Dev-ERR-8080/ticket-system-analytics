package com.hostel.dto;

import com.hostel.entity.Category;
import com.hostel.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintDTO {
    private Long id;
    private String title;
    private String description;
    private Category category;
    private Status status;
    private LocalDateTime createdAt;
    private String attachmentUrl;
    private String assignedTo;
    private UserDTO raisedBy;
}
