package com.app.brainmap.repositories;

import com.app.brainmap.domain.entities.ProjectFiles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProjectFileRepository extends JpaRepository<ProjectFiles, UUID> {

}
