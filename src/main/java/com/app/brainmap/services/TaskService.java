package com.app.brainmap.services;

import com.app.brainmap.domain.entities.Project;
import com.app.brainmap.domain.entities.Task;

import java.util.List;
import java.util.UUID;

public interface TaskService {

    List<Task> listTasks(UUID kanbanId);
    Task createTask(Task task);
    boolean updateTask(UUID id, Task task);
    boolean updateTaskColumn(UUID id, UUID columnId);
    boolean deleteTask(UUID id);
}
