package com.app.brainmap.repositories;

import com.app.brainmap.domain.UserRoleType;
import com.app.brainmap.domain.UserStatus;
import com.app.brainmap.domain.dto.UserProjectCountDto;
import com.app.brainmap.domain.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    long countByUserRole(UserRoleType userRole);
    long countByStatus(UserStatus status);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    List<User> findByUsernameContainingIgnoreCase(String username);
    List<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrUsernameContainingIgnoreCase(String firstName, String lastName, String username);


    @Query("SELECT new com.app.brainmap.domain.dto.UserProjectCountDto(u.id, COUNT(up.project)) " +
            "FROM User u LEFT JOIN u.userProjects up " +
            "GROUP BY u.id")
    List<UserProjectCountDto> findUsersWithProjectCount();

    @Override
    Page<User> findAll(Pageable pageable);

    @Query("""
        SELECT EXTRACT(YEAR FROM u.createdAt) AS year,
            EXTRACT(MONTH FROM u.createdAt) AS monthNumber,
            u.userRole AS role,
            COUNT(u) AS count
        FROM User u
        WHERE u.createdAt >= :startDate
        GROUP BY EXTRACT(YEAR FROM u.createdAt), EXTRACT(MONTH FROM u.createdAt), u.userRole
        ORDER BY EXTRACT(YEAR FROM u.createdAt), EXTRACT(MONTH FROM u.createdAt), u.userRole
""")
    List<Object[]> getMonthlyUserCountByRole(@Param("startDate") LocalDateTime startDate);

    @Query("""
SELECT 
EXTRACT(YEAR FROM u.createdAt) AS year,
EXTRACT(MONTH FROM u.createdAt) AS monthNumber,
COUNT(*) FILTER (WHERE u.userRole = 'MODERATOR') AS modaratorCount,
COUNT(*) FILTER (WHERE u.userRole = 'MENTOR') AS mentorCount,
COUNT(*) FILTER (WHERE u.userRole = 'PROJECT_MEMBER') AS projectMemberCount
FROM User u
GROUP BY EXTRACT(YEAR FROM u.createdAt), EXTRACT(MONTH FROM u.createdAt)
ORDER BY EXTRACT(YEAR FROM u.createdAt), EXTRACT(MONTH FROM u.createdAt)
""")
    List<Object[]> getUserGrowthByMonth(@Param("startDate") LocalDateTime startDate);

    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    long countByUserRoleAndCreatedAtBetween(UserRoleType userRoleType, LocalDateTime previousMonthStart, LocalDateTime start);

    long countByStatusAndCreatedAtBetween(UserStatus userStatus, LocalDateTime previousMonthStart, LocalDateTime start);

    @Query("SELECT u FROM User u WHERE u.userRole = 'PROJECT_MEMBER' AND LOWER(u.email) LIKE LOWER(CONCAT(:query, '%'))")
    List<User> searchMembers(@Param("query") String query);

    @Query("SELECT u FROM User u WHERE u.userRole = 'MENTOR' AND LOWER(u.email) LIKE LOWER(CONCAT(:query, '%'))")
    List<User> searchSupervisors(@Param("query") String query);

    Page<User> findByUserRole(UserRoleType userRole, Pageable pageable);

    Page<User> findAllByUserRole(UserRoleType userRole, Pageable pageable);

    @Query("""
            SELECT u FROM User u
            WHERE (:userRole IS NULL OR u.userRole = :userRole)
              AND (:userStatus IS NULL OR u.status = :userStatus)
              AND (COALESCE(:search, '') = '' OR (
                    LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
              ))
        """)
    Page<User> findByFilters(
            @Param("userRole") UserRoleType userRole,
            @Param("userStatus") UserStatus userStatus,
            @Param("search") String search,
            Pageable pageable);

}