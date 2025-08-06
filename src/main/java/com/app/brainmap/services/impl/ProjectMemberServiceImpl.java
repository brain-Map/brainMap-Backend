package com.app.brainmap.services.impl;

import com.app.brainmap.domain.entities.ProjectMember;
import com.app.brainmap.domain.entities.User;
import com.app.brainmap.repositories.ProjectMemberRepository;
import com.app.brainmap.repositories.UserRepository;
import com.app.brainmap.services.ProjectMemberService;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProjectMemberServiceImpl implements ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;

    public ProjectMemberServiceImpl(ProjectMemberRepository projectMemberRepository, UserRepository userRepository) {
        this.projectMemberRepository = projectMemberRepository;
        this.userRepository = userRepository;
    }


    @Override
    public Optional<ProjectMember> getProjectMember(UUID id) {
        return projectMemberRepository.findById(id);
    }

    @Override
    public ProjectMember updateAboutProjectMember(UUID id, String about) {
        // Step 1: Find the ProjectMember by ID
        ProjectMember existingProjectMember = projectMemberRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Project member not found with id: " + id));

        // Step 2: Get the associated User
        User user = existingProjectMember.getUser();
        if (user == null) {
            throw new IllegalStateException("No user associated with this project member");
        }


        user.setBio(about);


        userRepository.save(user); // assuming you have a userRepository

        // Step 5: (Optional) return the same ProjectMember, or modify if needed
        return existingProjectMember;
    }

}
