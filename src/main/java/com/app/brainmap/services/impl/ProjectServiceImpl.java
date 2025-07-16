package com.app.brainmap.services.impl;

import com.app.brainmap.domain.entities.Project;
import com.app.brainmap.repositories.ProjectRepositiory;
import com.app.brainmap.services.ProjectService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepositiory projectRepositiory;

    public ProjectServiceImpl(ProjectRepositiory projectRepositiory) {
        this.projectRepositiory = projectRepositiory;
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
        return projectRepositiory.save(new Project(
                null,
                project.getTitle(),
                project.getDescription(),
                null,
                "active",
                now,
                now,
                project.getPriority()

        ));

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
}
