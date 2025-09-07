package com.app.brainmap.controllers;

import com.app.brainmap.domain.dto.KanbanBoardColumnDto;
import com.app.brainmap.domain.dto.MessageResponse;
import com.app.brainmap.domain.dto.ProjectDto;
import com.app.brainmap.domain.dto.TaskDto;
import com.app.brainmap.domain.entities.Project;
import com.app.brainmap.domain.entities.Task;
import com.app.brainmap.mappers.TaskMapper;
import com.app.brainmap.services.ProjectService;
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

    public TaskController(TaskService taskService, TaskMapper taskMapper) {
        this.taskService = taskService;
        this.taskMapper = taskMapper;
    }

    @GetMapping("/{kanban_id}")
    public List<TaskDto> listTasks(@PathVariable("kanban_id") UUID kanbanId) {
        return taskService.listTasks(kanbanId)
                .stream()
                .map(taskMapper::toDto)
                .toList();
    }

    @PostMapping
    public TaskDto createTask(@RequestBody TaskDto taskDto) {
        Task createTask = taskService.createTask(
                taskMapper.toEntity(taskDto)
        );
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
