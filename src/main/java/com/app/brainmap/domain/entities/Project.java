package com.app.brainmap.domain.entities;

import com.app.brainmap.domain.ProjctStatus;
import com.app.brainmap.domain.ProjectPriority;
import com.app.brainmap.domain.ProjectPrivacy;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    @Column(name = "project_id" , nullable = false , updatable = false)
    private UUID id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User user;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ProjctStatus status;

    @Column(name = "privacy", updatable = true)
    @Enumerated(EnumType.STRING)
    private ProjectPrivacy privacy;

    private LocalDateTime createdAt;

    @Column(name= "due_date", nullable = true, updatable = true)
    private LocalDate dueDate;

    @Column(name = "priority", nullable = true, updatable = true)
    private ProjectPriority priority;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        if (this.status == null){
            this.status = ProjctStatus.ACTIVE;
        }
    }

    @OneToOne(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private KanbanBoard kanbanBoard;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserProject> userProjects = new ArrayList<>();

}
