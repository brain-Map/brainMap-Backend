package com.app.brainmap.domain.entities;

import com.app.brainmap.domain.ProjectPriority;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "projects")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID projectId;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    private String status;
    private LocalDateTime createdAt;

    @Column(name= "due_date", nullable = true, updatable = true)
    private LocalDateTime dueDate;

    @Column(name = "priority", nullable = true, updatable = true)
    private ProjectPriority priority;


    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
