package com.app.brainmap.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "tasks")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "task_id", updatable = false, nullable = false)
    private UUID taskId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDate createdDate;

    @Column(name = "created_time", nullable = false, updatable = false)
    private LocalTime createdTime;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "priority")
    private String priority = "MEDIUM";


    @ManyToOne
    @JoinColumn(name = "kanban_id",
            foreignKey = @ForeignKey(name = "fk_kanban_task",
                    foreignKeyDefinition = "FOREIGN KEY (kanban_id) REFERENCES kanaban_boards(kanban_id) ON DELETE CASCADE"))
    private KanbanBoard kanbanBoard;

    @ManyToOne
    @JoinColumn(name = "column_id",
            foreignKey = @ForeignKey(name = "fk_column_task",
                    foreignKeyDefinition = "FOREIGN KEY (column_id) REFERENCES kanban_columns(column_id) ON DELETE CASCADE"))
    private KanbanColumn kanbanColumn;



}
