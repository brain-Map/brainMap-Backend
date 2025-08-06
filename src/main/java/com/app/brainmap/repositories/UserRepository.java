package com.app.brainmap.repositories;

import com.app.brainmap.domain.UserRoleType;
import com.app.brainmap.domain.UserStatus;
import com.app.brainmap.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    long countByUserRole(UserRoleType userRole);
    long countByStatus(UserStatus status);
}