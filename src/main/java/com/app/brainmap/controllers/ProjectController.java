package com.app.brainmap.controllers;

import com.app.brainmap.domain.dto.ProjectDto;
import com.app.brainmap.domain.entities.Project;
import com.app.brainmap.mappers.ProjectMapper;
import com.app.brainmap.services.ProjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/project-member/projects")
@Slf4j
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
    public ProjectDto createProject(@RequestBody ProjectDto projectDto) {
        Project createProject = projectService.createProject(
                projectMapper.toEntity(projectDto)
        );
        return projectMapper.toDto(createProject);
    }

    @PutMapping(path="/{project_id}")
    public ProjectDto updateProject(
            @PathVariable("project_id") UUID id,
            @RequestBody ProjectDto projectDto
    ){
        Project project = projectMapper.toEntity(projectDto);
//        log.info("Updating project with id: {}, {}" , id , project.getId());
        Project updateProject = projectService.updateProject(
                id, project
        );


        return projectMapper.toDto(updateProject);

    }

    @DeleteMapping(path="/{project_id}")
    public void deleteProject(@PathVariable("project_id") UUID id){
//        log.info("Deleting project with id: {}", id);
        projectService.deleteProject(id);
    }
}
