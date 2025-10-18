package com.app.brainmap.repositories;

import com.app.brainmap.domain.entities.UserProject;
import com.app.brainmap.domain.entities.UserProjectCompositeKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserProjectRepository extends JpaRepository<UserProject, UserProjectCompositeKey> {
    List<UserProject> findAllByProjectId(UUID projectId);

    @Query("SELECT up FROM UserProject up " +
            "JOIN FETCH up.project p " +
            "WHERE up.user.id = :userId AND up.status = 'ACCEPTED' AND up.role != 'OWNER'")
    List<UserProject> findAcceptedProjectsByUser(@Param("userId") UUID userId);

    List<UserProject> findAllByProjectIdAndRole(UUID projectId, com.app.brainmap.domain.ProjectPositionType role);

    // java
    Optional<UserProject> findByUserIdAndProjectId(UUID userId, UUID projectId);


}
