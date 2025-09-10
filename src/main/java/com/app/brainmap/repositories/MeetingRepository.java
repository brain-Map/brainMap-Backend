package com.app.brainmap.repositories;

import com.app.brainmap.domain.entities.Meeting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, UUID> {

    boolean existsByRoomName(String roomName);

    Optional<Meeting> findByRoomName(String roomName);

    @Query("SELECT m FROM Meeting m WHERE m.createdBy = :userId ORDER BY m.createdAt DESC")
    Page<Meeting> findByCreatedByOrderByCreatedAtDesc(@Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT m FROM Meeting m WHERE m.createdBy = :userId AND m.isActive = :isActive ORDER BY m.createdAt DESC")
    Page<Meeting> findByCreatedByAndIsActiveOrderByCreatedAtDesc(
            @Param("userId") UUID userId, 
            @Param("isActive") Boolean isActive, 
            Pageable pageable);

    @Query("SELECT m FROM Meeting m WHERE m.isActive = true ORDER BY m.createdAt DESC")
    List<Meeting> findActiveMeetings();

    @Query("SELECT COUNT(m) FROM Meeting m WHERE m.isActive = true")
    long countActiveMeetings();
}
