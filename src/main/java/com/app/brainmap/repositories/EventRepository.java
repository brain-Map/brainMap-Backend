package com.app.brainmap.repositories;

import com.app.brainmap.domain.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {

    // Method 1: Find events by single date - Spring Data JPA auto-generates query
    List<Event> findByDueDateOrderByDueTimeAsc(LocalDate dueDate);

    // Method 2: Find events between date range with custom query
    @Query("SELECT e FROM Event e WHERE e.dueDate BETWEEN :startDate AND :endDate ORDER BY e.dueDate ASC, e.dueTime ASC")
    List<Event> findByDueDateBetweenOrderByDueDateAscDueTimeAsc(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Method 3: Count events by single date - Spring Data JPA auto-generates query
    long countByDueDate(LocalDate dueDate);
}