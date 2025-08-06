package com.app.brainmap.services;

import com.app.brainmap.domain.entities.ProjectMember;

import java.util.Optional;
import java.util.UUID;

public interface ProjectMemberService {

    Optional<ProjectMember> getProjectMember(UUID id);
    ProjectMember updateAboutProjectMember(UUID id, String about);

}
