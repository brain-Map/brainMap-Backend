package com.app.brainmap.repositories;

import com.app.brainmap.domain.entities.EventProject;
import com.app.brainmap.domain.entities.ProjectFiles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProjectFileRepository extends JpaRepository<ProjectFiles, UUID> {
    // If you can project directly to EventProjectDto:
    List<ProjectFiles> findAllByProject_Id(UUID projectId);
}
