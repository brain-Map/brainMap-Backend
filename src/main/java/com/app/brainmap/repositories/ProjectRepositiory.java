package com.app.brainmap.repositories;


import com.app.brainmap.domain.ProjctStatus;
import com.app.brainmap.domain.UserStatus;
import com.app.brainmap.domain.entities.Project;
import com.app.brainmap.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectRepositiory extends JpaRepository<Project, UUID> {
    long countByStatus(ProjctStatus status);

    List<Project> user(User user);

    @Query("SELECT p FROM Project p WHERE p.user.id = :ownerId")
    List<Project> findAllByOwnerId(UUID ownerId);
}
