package com.app.brainmap.services;

import com.app.brainmap.domain.entities.Project;

import java.util.List;

public interface ProjectService {

    List<Project> listProject();
    Project createProject(Project project);

}
