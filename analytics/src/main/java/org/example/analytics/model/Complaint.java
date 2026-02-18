package org.example.analytics.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "complaints")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String assignedTo;

    private LocalDate availabilityDate;

    private String block;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    private String contactNo;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType messageType;

    private String roomNo;

    private String roomType;

    private String specificCategory;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    private String subBlock;

    private String subCategory;

    private String timeSlot;

    @Column(nullable = false)
    private Long raisedBy;
}