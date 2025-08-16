package com.app.brainmap.repositories;

import com.app.brainmap.domain.entities.UserProject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserProjectRepository extends JpaRepository<UserProject, UUID> {
    List<UserProject> findAllByProjectId(UUID projectId);
}
