package com.app.brainmap.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "educations")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Education {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "education_id", updatable = false, nullable = false)
    private UUID educationId;

    @Column(name = "title", nullable = false, columnDefinition = "TEXT")
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(255) DEFAULT 'Pending'")
    private String status;

    @ManyToOne
    @JoinColumn(name = "mentor_id")
    private User mentor;


}
