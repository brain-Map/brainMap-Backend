package com.app.brainmap.controllers;

import com.app.brainmap.domain.dto.ProjectDto;
import com.app.brainmap.domain.entities.Project;
import com.app.brainmap.mappers.ProjectMapper;
import com.app.brainmap.services.ProjectService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/project-member/projects")
public class ProjectController {
    private final ProjectService projectService;
    private final ProjectMapper projectMapper;


    public ProjectController(ProjectService projectService, ProjectMapper projectMapper) {
        this.projectService = projectService;
        this.projectMapper = projectMapper;
    }

    @PostMapping
    public ProjectDto createTaskList(@RequestBody ProjectDto projectDto) {
        Project createProject = projectService.createProject(
                projectMapper.toEntity(projectDto)
        );
        return projectMapper.toDto(createProject);
    }
}
