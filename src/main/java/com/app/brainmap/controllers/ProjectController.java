package com.app.brainmap.controllers;

import com.app.brainmap.domain.dto.*;
import com.app.brainmap.domain.entities.Project;
import com.app.brainmap.mappers.KanbanBoardMapper;
import com.app.brainmap.mappers.ProjectMapper;
import com.app.brainmap.mappers.UserProjectMapper;
import com.app.brainmap.services.ProjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(path = "/project-member/projects")
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
public class ProjectController {
    private final ProjectService projectService;
    private final ProjectMapper projectMapper;
    private final KanbanBoardMapper kanbanBoardMapper;
    private final UserProjectMapper userProjectMapper;


    public ProjectController(ProjectService projectService, ProjectMapper projectMapper, KanbanBoardMapper kanbanBoardMapper, UserProjectMapper userProjectMapper) {
        this.projectService = projectService;
        this.projectMapper = projectMapper;
        this.kanbanBoardMapper = kanbanBoardMapper;
        this.userProjectMapper = userProjectMapper;
    }

    @GetMapping(path = "/all/{user_id}")
    public List<ProjectDto> listProject(@PathVariable("user_id") UUID userId) {
        return projectService.listProject(userId)
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

    @GetMapping("/{project_id}")
    public Optional<ProjectDto> getProject(@PathVariable("project_id") UUID projectId) {
        return projectService.getProject(projectId).map(projectMapper::toDto);

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



    @GetMapping("/kanban-board/{project_id}")
    public ResponseEntity<KanbanBoardDto> getKanbanBoardDetails(@PathVariable("project_id") UUID projectId) {
        return projectService.getKanbanBoardDetails(projectId)
                .map(kanbanBoardMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping(path = "/kanban-board/{project_id}")
    public ResponseEntity<MessageResponse> updateKanbanBoard(
            @PathVariable("project_id") UUID projectId,
            @RequestBody KanbanBoardColumnDto kanbanBoardColumnDto
    ) {
        boolean isUpdated = projectService.updateKanbanColumn(
                projectId,
                kanbanBoardMapper.toEntity(kanbanBoardColumnDto) // maps DTO → KanbanColumn
        );

        if (isUpdated) {
            return ResponseEntity.ok(new MessageResponse("Kanban board updated successfully"));
        } else {
            return ResponseEntity.status(404).body(new MessageResponse("Project not found"));
        }
    }

    @DeleteMapping("/kanban-board/{project_id}")
    public ResponseEntity<MessageResponse> deleteKanbanBoard(@PathVariable("project_id") UUID projectId,
                                                             @RequestBody KanbanBoardColumnDto kanbanBoardColumnDto) {

        UUID columnId = kanbanBoardColumnDto.columnId();

        boolean isDeleted = projectService.deleteKanbanBoardColumn(columnId);
        if (isDeleted) {
            return ResponseEntity.ok(new MessageResponse("Kanban board column deleted successfully"));
        } else {
            return ResponseEntity.status(404).body(new MessageResponse("Kanban board column not found"));
        }
    }

    @GetMapping(path = "/collaborators/{project_id}")
    public List<UserProjectDto> listCollaborators(@PathVariable("project_id") UUID projectId) {
        return projectService.listUserProject(projectId)
                .stream()
                .map(userProjectMapper::toDto)
                .toList();
    }



}
