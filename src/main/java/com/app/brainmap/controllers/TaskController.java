package com.app.brainmap.controllers;

import com.app.brainmap.domain.dto.ProjectDto;
import com.app.brainmap.domain.dto.TaskDto;
import com.app.brainmap.domain.entities.Project;
import com.app.brainmap.domain.entities.Task;
import com.app.brainmap.mappers.TaskMapper;
import com.app.brainmap.services.ProjectService;
import com.app.brainmap.services.TaskService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @GetMapping
    public List<TaskDto> listTasks() {
        return taskService.listTasks()
                .stream()
                .map(taskMapper::toDto)
                .toList();
    }

    @PostMapping
    public TaskDto createProject(@RequestBody TaskDto taskDto) {
        Task createTask = taskService.createTask(
                taskMapper.toEntity(taskDto)
        );
        return taskMapper.toDto(createTask);
    }

    @PutMapping("/{task_id}")
    public TaskDto updateTask(
            @PathVariable("task_id") UUID id,
            @RequestBody TaskDto taskDto
    ) {
        Task updatedTask = taskService.updateTask(
                id,
                taskMapper.toEntity(taskDto)
        );
        return taskMapper.toDto(updatedTask);
    }

}
