package com.app.brainmap.services.impl;

import com.app.brainmap.domain.entities.ProjectMember;
import com.app.brainmap.repositories.ProjectMemberRepository;
import com.app.brainmap.services.ProjectMemberService;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ProjectMemberServiceImpl implements ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;

    public ProjectMemberServiceImpl(ProjectMemberRepository projectMemberRepository) {
        this.projectMemberRepository = projectMemberRepository;
    }


    @Override
    public Optional<ProjectMember> getProjectMember(UUID id) {
        return projectMemberRepository.findById(id);
    }
}
