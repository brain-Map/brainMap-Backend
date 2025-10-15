package com.app.brainmap.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "kanban_columns")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class KanbanColumn {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "column_id", updatable = false, nullable = false)
    private UUID columnId;

    @Column(name = "type", nullable = false)
    private String type;

    @ManyToOne
    @JoinColumn(name = "kanban_id",
            foreignKey = @ForeignKey(name = "fk_kanban_task",
                    foreignKeyDefinition = "FOREIGN KEY (kanban_id) REFERENCES kanaban_boards(kanban_id) ON DELETE CASCADE"))
    private KanbanBoard kanbanBoard;
}
