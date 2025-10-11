package com.app.brainmap.repositories;

import com.app.brainmap.domain.entities.UserTask;
import com.app.brainmap.domain.entities.UserTaskCompositeKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserTaskRepository extends JpaRepository<UserTask, UserTaskCompositeKey> {
    List<UserTask> findByTask_TaskId(UUID taskId);
}
