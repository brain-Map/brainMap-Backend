package com.app.brainmap.repositories;

import com.app.brainmap.domain.entities.KanbanBoard;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface KanbanBoardRepository extends JpaRepository<KanbanBoard, UUID> {

    // This ensures columns are eagerly fetched with the board
    Optional<KanbanBoard> findByProjectId(UUID projectId);
}
