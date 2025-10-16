package com.app.brainmap.repositories;

import com.app.brainmap.domain.entities.Chat.Group;
import com.app.brainmap.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface GroupRepository extends JpaRepository<Group, UUID> {
    @Query("SELECT g FROM Group g JOIN g.members m WHERE m.id = :userId")
    List<Group> findGroupsByMemberId(@Param("userId") UUID userId);

    @Query("SELECT g FROM Group g WHERE g.project.id = :projectId")
    Group findByProjectId(@Param("projectId") UUID projectId);
}
