package com.app.brainmap.services.impl;

import com.app.brainmap.domain.entities.Project;
import com.app.brainmap.repositories.ProjectRepositiory;
import com.app.brainmap.services.ProjectService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
        if(null != project.getProjectId()){
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
}
