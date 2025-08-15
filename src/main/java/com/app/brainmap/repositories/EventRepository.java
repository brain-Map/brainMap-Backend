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
    List<Event> findByDueDateOrderByCreatedTimeAsc(LocalDate date);

    @Query("SELECT e FROM Event e WHERE e.dueDate BETWEEN :startDate AND :endDate ORDER BY e.createdTime ASC")

    List<Event> findEventsByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);//CHECK

    long countByDueDate(LocalDate dueDate);//CHECK
}