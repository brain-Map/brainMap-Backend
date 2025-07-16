package com.app.brainmap.controllers;

import com.app.brainmap.domain.dto.ProjectDto;
import com.app.brainmap.domain.entities.Project;
import com.app.brainmap.mappers.ProjectMapper;
import com.app.brainmap.services.ProjectService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/project-member/projects")
public class ProjectController {
    private final ProjectService projectService;
    private final ProjectMapper projectMapper;


    public ProjectController(ProjectService projectService, ProjectMapper projectMapper) {
        this.projectService = projectService;
        this.projectMapper = projectMapper;
    }

    @GetMapping
    public List<ProjectDto> listProject() {
        return projectService.listProject()
                .stream()
                .map(projectMapper::toDto)
                .toList();
    }

    @PostMapping
    public ProjectDto createTaskList(@RequestBody ProjectDto projectDto) {
        Project createProject = projectService.createProject(
                projectMapper.toEntity(projectDto)
        );
        return projectMapper.toDto(createProject);
    }
}
