package com.app.brainmap.services.impl;

import com.app.brainmap.domain.entities.*;
import com.app.brainmap.repositories.TaskRepository;
import com.app.brainmap.repositories.UserTaskRepository;
import com.app.brainmap.services.TaskService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserTaskRepository userTaskRepository;
    public TaskServiceImpl(TaskRepository taskRepository, UserTaskRepository userTaskRepository) {
        this.taskRepository = taskRepository;
        this.userTaskRepository = userTaskRepository;
    }

    @Override
    public List<Task> listTasks(UUID kanbanId) {
        return taskRepository.findByKanbanBoard_KanbanId(kanbanId);
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
    public boolean updateTaskColumn(UUID id, UUID columnId) {
        Optional<Task> taskOptional = taskRepository.findById(id);
        if (taskOptional.isEmpty()) {
            return false;
        }

        Task taskfind = taskOptional.get();

        KanbanColumn columnRef = new KanbanColumn();
        columnRef.setColumnId(columnId); // just set the ID
        taskfind.setKanbanColumn(columnRef);

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

    @Override
    public void assignUserToTask(String userId, UUID taskId) {
        UUID userUuid = UUID.fromString(userId); // Convert String to UUID
        UserTaskCompositeKey key = new UserTaskCompositeKey(userUuid, taskId);
        User user = new User();
        user.setId(userUuid);
        Task task = new Task();
        task.setTaskId(taskId);

        UserTask userTask = new UserTask();
        userTask.setId(key);
        userTask.setUser(user);
        userTask.setTask(task);

        userTaskRepository.save(userTask);
    }


}
