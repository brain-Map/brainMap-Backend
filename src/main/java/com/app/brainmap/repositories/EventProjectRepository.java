package com.app.brainmap.repositories;

import com.app.brainmap.domain.dto.EventProjectDto;
import com.app.brainmap.domain.entities.EventProject;
import com.app.brainmap.domain.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EventProjectRepository extends JpaRepository<EventProject, UUID> {
    // If you can project directly to EventProjectDto:
    List<EventProject> findAllByProject_Id(UUID projectId);
}
