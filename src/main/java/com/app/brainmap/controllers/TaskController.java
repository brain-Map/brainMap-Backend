package com.app.brainmap.controllers;

import com.app.brainmap.domain.dto.MessageResponse;
import com.app.brainmap.domain.dto.TaskDto;
import com.app.brainmap.domain.entities.Task;
import com.app.brainmap.mappers.TaskMapper;
import com.app.brainmap.repositories.UserTaskRepository;
import com.app.brainmap.services.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(path="/api/tasks")
public class TaskController {

    private final TaskService taskService;
    private final TaskMapper taskMapper;
    private final UserTaskRepository userTaskRepository;

    public TaskController(TaskService taskService, TaskMapper taskMapper, UserTaskRepository userTaskRepository) {
        this.userTaskRepository = userTaskRepository;
        this.taskService = taskService;
        this.taskMapper = taskMapper;
    }

    @GetMapping("/{kanban_id}")
    public List<TaskDto> listTasks(@PathVariable("kanban_id") UUID kanbanId) {
        // 1️⃣ Get all tasks for the kanban board
        List<Task> tasks = taskService.listTasks(kanbanId);

        // 2️⃣ Map each task to TaskDto with assignees
        List<TaskDto> taskDtos = tasks.stream().map(task -> {
            // Fetch assignees from UserTask table
            List<String> assignees = userTaskRepository.findByTask_TaskId(task.getTaskId())
                    .stream()
                    .map(ut -> ut.getUser().getId().toString())
                    .toList();

            // Map the task to DTO
            TaskDto dto = taskMapper.toDto(task);

            // Create a new DTO with assignees filled
            return new TaskDto(
                    dto.taskId(),
                    dto.kanbanId(),
                    dto.kanbanColumnId(),
                    dto.title(),
                    dto.description(),
                    dto.createdDate(),
                    dto.createdTime(),
                    dto.dueDate(),
                    dto.priority(),
                    assignees
            );
        }).toList();

        return taskDtos;
    }


    @PostMapping
    public TaskDto createTask(@RequestBody TaskDto taskDto) {
        Task createTask = taskService.createTask(
                taskMapper.toEntity(taskDto)
        );

        // Save assignees to user_task table
        if (taskDto.assignees() != null) {
            for (String userId : taskDto.assignees()) {
                taskService.assignUserToTask(userId, createTask.getTaskId());
            }
        }
        return taskMapper.toDto(createTask);
    }



    @PutMapping("/{task_id}")
    public ResponseEntity<MessageResponse> updateTask(
            @PathVariable("task_id") UUID taskId,
            @RequestBody TaskDto taskDto
    ) {
        boolean isUpdated = taskService.updateTask(
                taskId,
                taskMapper.toEntity(taskDto) // maps DTO → KanbanColumn
        );

        if (isUpdated) {
            return ResponseEntity.ok(new MessageResponse("task updated successfully"));
        } else {
            return ResponseEntity.status(404).body(new MessageResponse("task not found"));
        }
    }

    @PutMapping("column/{task_id}")
    public ResponseEntity<MessageResponse> updateTaskColumn(
            @PathVariable("task_id") UUID taskId,
            @RequestBody Map<String, UUID> body
    ) {
        UUID columnId = body.get("columnId"); // get value from JSON

        boolean isUpdated = taskService.updateTaskColumn(taskId, columnId);

        if (isUpdated) {
            return ResponseEntity.ok(new MessageResponse("task updated successfully"));
        } else {
            return ResponseEntity.status(404).body(new MessageResponse("task not found"));
        }
    }




    @DeleteMapping
    public ResponseEntity<MessageResponse> deleteTask(@RequestBody TaskDto taskDto) {

        UUID taskId = taskDto.taskId();

        boolean isDeleted = taskService.deleteTask(taskId);
        if (isDeleted) {
            return ResponseEntity.ok(new MessageResponse("Task deleted successfully"));
        } else {
            return ResponseEntity.status(404).body(new MessageResponse("Task not found"));
        }
    }

}
