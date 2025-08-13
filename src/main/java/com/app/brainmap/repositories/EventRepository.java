package com.app.brainmap.repositories;

import com.app.brainmap.domain.entities.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {

    List<Event> findByUser_IdOrderByDueDateAsc(UUID userId);

    List<Event> findByUser_IdAndDueDateOrderByCreatedTimeAsc(UUID userId, LocalDate dueDate);

    @Query("SELECT e FROM Event e WHERE e.user.id = :userId AND e.dueDate BETWEEN :startDate AND :endDate ORDER BY e.dueDate ASC, e.createdTime ASC")
    List<Event> findEventsByUserIdAndDateRange(@Param("userId") UUID userId,
                                               @Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);

    Page<Event> findByUser_IdOrderByDueDateDesc(UUID userId, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.user.id = :userId AND (LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Event> searchEventsByUserIdAndKeyword(@Param("userId") UUID userId, @Param("keyword") String keyword, Pageable pageable);

    Optional<Event> findByEventIdAndUser_Id(UUID eventId, UUID userId);

    long countByUser_Id(UUID userId);

    @Query("SELECT COUNT(e) FROM Event e WHERE e.user.id = :userId AND e.dueDate = :date")
    long countEventsByUserIdAndDate(@Param("userId") UUID userId, @Param("date") LocalDate date);
}