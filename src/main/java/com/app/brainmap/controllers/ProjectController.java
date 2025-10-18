package com.app.brainmap.controllers;

import com.app.brainmap.domain.dto.*;
import com.app.brainmap.domain.dto.DomainExpert.ServiceBookingResponseDto;
import com.app.brainmap.domain.dto.MessageResponse;
import com.app.brainmap.domain.dto.Project.AllProjectUserDto;
import com.app.brainmap.domain.dto.ProjectMember.BookingDetailsDto;
import com.app.brainmap.domain.entities.Project;
import com.app.brainmap.mappers.CollaborateProjectMapper;
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
public class ProjectController {
    private final ProjectService projectService;
    private final ProjectMapper projectMapper;
    private final KanbanBoardMapper kanbanBoardMapper;
    private final UserProjectMapper userProjectMapper;
    private final CollaborateProjectMapper collaborateProjectMapper;

    public ProjectController(ProjectService projectService, ProjectMapper projectMapper, KanbanBoardMapper kanbanBoardMapper, UserProjectMapper userProjectMapper, CollaborateProjectMapper collaborateProjectMapper) {
        this.projectService = projectService;
        this.projectMapper = projectMapper;
        this.kanbanBoardMapper = kanbanBoardMapper;
        this.userProjectMapper = userProjectMapper;
        this.collaborateProjectMapper = collaborateProjectMapper;
    }

    @GetMapping(path = "/all/{user_id}")
    public List<ProjectDto> listProject(@PathVariable("user_id") UUID userId) {
        return projectService.listProject(userId)
                .stream()
                .map(projectMapper::toDto)
                .toList();
    }

    @GetMapping("/collaborator")
    public List<AllProjectUserDto> getAcceptedProjects(@RequestParam UUID userId) {
        return projectService.getAcceptedProjects(userId)
                .stream()
                .map(collaborateProjectMapper::toDto)
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

    @DeleteMapping(path="/collaborators/{project_id}/{user_id}")
    public ResponseEntity<MessageResponse> removeCollaborator(
            @PathVariable("project_id") UUID projectId,
            @PathVariable("user_id") UUID userId
    ){
        projectService.removeCollaborator(projectId, userId);
        return ResponseEntity.ok(new MessageResponse("Collaborator removed successfully"));
    }


    @GetMapping(path ="/hired-expert/{user_id}")
    public List<BookingDetailsDto> listHiredExperts(@PathVariable("user_id") UUID userId) {
        return projectService.listHiredExperts(userId)
                .stream()
                .toList();
    }

    @GetMapping("/owners/{project_id}")
    public List<UserProjectDto> getProjectOwners(@PathVariable("project_id") UUID projectId) {
        return projectService.getProjectOwners(projectId)
                .stream()
                .map(userProjectMapper::toDto)
                .toList();
    }

    @GetMapping("/{bookingId}/bookings/filter")
    public ResponseEntity<List<ServiceBookingResponseDto>> getFilteredBookingsForDomainExpert(
            @PathVariable UUID bookingId) {
        List<ServiceBookingResponseDto> bookings = projectService.getBookingsForDomainExpertFiltered(bookingId);
        return ResponseEntity.ok(bookings);
    }



}
