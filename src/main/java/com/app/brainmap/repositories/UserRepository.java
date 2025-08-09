package com.app.brainmap.repositories;

import com.app.brainmap.domain.UserRoleType;
import com.app.brainmap.domain.UserStatus;
import com.app.brainmap.domain.dto.UserProjectCountDto;
import com.app.brainmap.domain.entities.User;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    long countByUserRole(UserRoleType userRole);
    long countByStatus(UserStatus status);


    @Query("SELECT new com.app.brainmap.domain.dto.UserProjectCountDto(u.id, COUNT(up.project)) " +
            "FROM User u LEFT JOIN u.userProjects up " +
            "GROUP BY u.id")
    List<UserProjectCountDto> findUsersWithProjectCount();

    @Override
    Page<User> findAll(Pageable pageable);
}