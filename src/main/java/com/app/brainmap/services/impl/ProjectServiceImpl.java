package com.app.brainmap.services.impl;

import com.app.brainmap.domain.entities.KanbanBoard;
import com.app.brainmap.domain.entities.KanbanColumn;
import com.app.brainmap.domain.entities.Project;
import com.app.brainmap.repositories.KanbanBoardRepository;
import com.app.brainmap.repositories.KanbanColumnRepository;
import com.app.brainmap.repositories.ProjectRepositiory;
import com.app.brainmap.services.ProjectService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepositiory projectRepositiory;
    private final KanbanBoardRepository kanbanBoardRepository;
    private final KanbanColumnRepository kanbanColumnRepository;

    public ProjectServiceImpl(ProjectRepositiory projectRepositiory, KanbanBoardRepository kanbanBoardRepository, KanbanColumnRepository kanbanColumnRepository) {
        this.projectRepositiory = projectRepositiory;
        this.kanbanBoardRepository = kanbanBoardRepository;
        this.kanbanColumnRepository = kanbanColumnRepository;
    }

    @Override
    public List<Project> listProject() {
        return projectRepositiory.findAll();
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

        return savedProject;

    }

    @Override
    public Optional<Project> getProject(UUID id) {
        return projectRepositiory.findById(id);
    }

    @Override
    public Project updateProject(UUID id, Project project) {

        if(project.getId() == null){
            throw new IllegalArgumentException("Project must have and id");
        }

        if(!Objects.equals(project.getId(), id)){
            throw new IllegalArgumentException("Attempting to change project ID, this is not permitted!");
        }

        Project existingProject =  projectRepositiory.findById(id).orElseThrow(()->
                new IllegalArgumentException("project not found"));

        existingProject.setTitle(project.getTitle());
        existingProject.setDescription(project.getDescription());
        existingProject.setCreatedAt(LocalDateTime.now());
        existingProject.setPriority(project.getPriority());
        existingProject.setStatus(project.getStatus());
        existingProject.setDueDate(project.getDueDate());
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


}
