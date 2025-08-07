package com.app.brainmap.repositories;

import com.app.brainmap.domain.entities.KanbanColumn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface KanbanColumnRepository extends JpaRepository<KanbanColumn, UUID> {}