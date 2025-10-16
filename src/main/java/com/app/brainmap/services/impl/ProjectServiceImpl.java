package com.app.brainmap.services.impl;

import com.app.brainmap.domain.ProjectCollaboratorAccept;
import com.app.brainmap.domain.ProjectPositionType;
import com.app.brainmap.domain.dto.ProjectMember.BookingDetailsDto;
import com.app.brainmap.domain.entities.*;
import com.app.brainmap.domain.entities.DomainExpert.ServiceBooking;
import com.app.brainmap.repositories.*;
import com.app.brainmap.services.ProjectService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepositiory projectRepositiory;
    private final KanbanBoardRepository kanbanBoardRepository;
    private final KanbanColumnRepository kanbanColumnRepository;
    private final UserProjectRepository userProjectRepository;
    private final ServiceBookingRepository serviceBookingRepository;

    public ProjectServiceImpl(ProjectRepositiory projectRepositiory, KanbanBoardRepository kanbanBoardRepository, KanbanColumnRepository kanbanColumnRepository, UserProjectRepository userProjectRepository, ServiceBookingRepository serviceBookingRepository) {
        this.projectRepositiory = projectRepositiory;
        this.kanbanBoardRepository = kanbanBoardRepository;
        this.kanbanColumnRepository = kanbanColumnRepository;
        this.userProjectRepository = userProjectRepository;

        this.serviceBookingRepository = serviceBookingRepository;
    }

    @Override
    public List<Project> listProject(UUID userId) {
        return projectRepositiory.findAllByOwnerId(userId);
    }

    @Override
    public List<UserProject> getAcceptedProjects(UUID userId) {
        return userProjectRepository.findAcceptedProjectsByUser(userId);
    }


    @Override
    public Project createProject(Project project) {
        if(null != project.getId()){
            throw new IllegalArgumentException("Project Id must be null");
        }

//        if(null == project.getTitle() || project.getTitle().isBlank()){
//            throw new IllegalArgumentException("Project title must not be null or empty");
//        }

        LocalDateTime now = LocalDateTime.now();
        Project savedProject= projectRepositiory.save(new Project(
                null,
                project.getTitle(),
                project.getDescription(),
                project.getUser(),
                project.getStatus(),
                project.getPrivacy(),
                now,
                project.getDueDate(),
                project.getPriority(),
                null,
                null
        ));


        // Create the kanban board for the project
        KanbanBoard kanbanBoard = KanbanBoard.builder()
                .project(savedProject)
                .build();

        // Create default columns
        List<KanbanColumn> defaultColumns = List.of(
                KanbanColumn.builder().type("To Do").kanbanBoard(kanbanBoard).build(),
                KanbanColumn.builder().type("In Progress").kanbanBoard(kanbanBoard).build(),
                KanbanColumn.builder().type("Done").kanbanBoard(kanbanBoard).build()
        );

        kanbanBoard.setColumns(defaultColumns);
        // Save the kanban board (columns will be saved via cascade)
        kanbanBoardRepository.save(kanbanBoard);

        UserProjectCompositeKey key = new UserProjectCompositeKey(
                savedProject.getUser().getId(),
                savedProject.getId()
        );

        UserProject userProject = UserProject.builder()
                .id(key)
                .user(savedProject.getUser())
                .project(savedProject)
                .role(ProjectPositionType.OWNER)
                .status(ProjectCollaboratorAccept.ACCEPTED)
                .build();

        userProjectRepository.save(userProject);

        return savedProject;

    }

    @Override
    public Optional<Project> getProject(UUID id) {
        return projectRepositiory.findById(id);
    }

    @Override
    public Project updateProject(UUID id, Project project) {
        Project existingProject = projectRepositiory.findById(id).orElseThrow(() ->
                new IllegalArgumentException("project not found"));

        if (project.getTitle() != null) {
            existingProject.setTitle(project.getTitle());
        }
        if (project.getDescription() != null) {
            existingProject.setDescription(project.getDescription());
        }
        if (project.getPriority() != null) {
            existingProject.setPriority(project.getPriority());
        }
        if (project.getStatus() != null) {
            existingProject.setStatus(project.getStatus());
        }
        if (project.getDueDate() != null) {
            existingProject.setDueDate(project.getDueDate());
        }
        if(project.getPrivacy() != null){
            existingProject.setPrivacy(project.getPrivacy());
        }

        // Usually `createdAt` should NOT be updated on update.
        // Instead, you might want to update `updatedAt`.
//        existingProject.setUpdatedAt(LocalDateTime.now());

        return projectRepositiory.save(existingProject);
    }


    @Override
    public void deleteProject(UUID projectId) {
        projectRepositiory.deleteById(projectId);
    }

    @Override
    public Optional<KanbanBoard> getKanbanBoardDetails(UUID projectId) {
        return kanbanBoardRepository.findByProjectId(projectId);
    }

    @Override
    public boolean updateKanbanColumn(UUID projectId, KanbanColumn kanbanColumn) {

        Optional<KanbanBoard> kanbanBoardOpt = kanbanBoardRepository.findByProjectId(projectId);

        if (kanbanBoardOpt.isEmpty()) {
            return false;
        }

        KanbanBoard kanbanBoard = kanbanBoardOpt.get();

        // If you just want the ID
//        UUID kanbanId = kanbanBoard.getKanbanId();

        // Link column to board
        kanbanColumn.setKanbanBoard(kanbanBoard);

        kanbanColumnRepository.save(kanbanColumn);

        return true;
    }

    @Override
    public boolean deleteKanbanBoardColumn(UUID columnId) {
        Optional<KanbanColumn> kanbanColumnOptional = kanbanColumnRepository.findById(columnId);

        if (kanbanColumnOptional.isEmpty()) {
            return false;
        }

        KanbanColumn kanbanColumn = kanbanColumnOptional.get();
        kanbanColumnRepository.delete(kanbanColumn);
        return true;
    }

    @Override
    public List<UserProject> listUserProject(UUID projectId) {
        return userProjectRepository.findAllByProjectId(projectId);
    }

    @Override
    public List<BookingDetailsDto> listHiredExperts(UUID userId) {
        return serviceBookingRepository.findAllBookingDetailsByUserId(userId);

    }
      
    public List<UserProject> getProjectOwners(UUID projectId) {
        return userProjectRepository.findAllByProjectIdAndRole(projectId, ProjectPositionType.OWNER);
    }


}
