package com.app.brainmap.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "kanaban_boards")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class KanbanBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "kanban_id", updatable = false, nullable = false)
    private UUID kanbanId;


    @OneToOne
    @JoinColumn(name = "project_id", nullable = false, foreignKey = @ForeignKey(name = "fk_project_kanban", foreignKeyDefinition = "FOREIGN KEY (project_id) REFERENCES projects(project_id) ON DELETE CASCADE"))
    private Project project;

    @OneToMany(mappedBy = "kanbanBoard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<KanbanColumn> columns = new ArrayList<>();

    @OneToMany(mappedBy = "kanbanBoard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();



}
