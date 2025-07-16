package com.app.brainmap.services;

import com.app.brainmap.domain.entities.Project;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectService {

    List<Project> listProject();

    Project createProject(Project project);
    Optional<Project> getProject(UUID id);
    Project updateProject(UUID projectId, Project project);
    void deleteProject(UUID projectId);


}
