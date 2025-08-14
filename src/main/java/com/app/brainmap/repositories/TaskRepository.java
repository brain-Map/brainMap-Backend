package com.app.brainmap.repositories;


import com.app.brainmap.domain.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findByKanbanBoard_KanbanId(UUID kanbanId);
}
