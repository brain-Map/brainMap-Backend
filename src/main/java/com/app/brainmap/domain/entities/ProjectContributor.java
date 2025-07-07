package com.app.brainmap.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "project_contributors")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ProjectContributor {
     @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID contributorId;

    @ManyToOne
    private Project project;

    @ManyToOne
    private User user;

    private String role;
    private LocalDateTime joinedAt;

    @PrePersist
    public void prePersist() {
        joinedAt = LocalDateTime.now();
    }
}
