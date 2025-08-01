package com.app.brainmap.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "qualifications")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Qualification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "qualification_id", updatable = false, nullable = false)
    private UUID educationId;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(255) DEFAULT 'Pending'")
    private String status;

    @ManyToOne
    @JoinColumn(name = "expert_id")
    private User expert;


}
