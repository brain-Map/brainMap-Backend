package com.app.brainmap.services.impl;

import com.app.brainmap.domain.entities.KanbanBoard;
import com.app.brainmap.domain.entities.Task;
import com.app.brainmap.repositories.TaskRepository;
import com.app.brainmap.services.TaskService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public List<Task> listTasks() {
        return taskRepository.findAll();
    }

    @Override
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public boolean updateTask(UUID id, Task task) {

        Optional<Task> taskOptional = taskRepository.findById(id);

        if (taskOptional.isEmpty()) {
            return false;
        }
        Task taskfind = taskOptional.get();

        // If you just want the ID
//        UUID kanbanId = kanbanBoard.getKanbanId();

        taskfind.setTitle(task.getTitle());
        taskfind.setDescription(task.getDescription());

        taskRepository.save(taskfind);

        return true;
    }

    @Override
    public boolean deleteTask(UUID id) {
        Optional<Task> taskOptional = taskRepository.findById(id);

        if (taskOptional.isEmpty()) {
            return false;
        }

        Task task = taskOptional.get();
        taskRepository.delete(task);
        return true;
    }
}
