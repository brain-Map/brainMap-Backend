package com.app.brainmap.domain.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_project")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
}
