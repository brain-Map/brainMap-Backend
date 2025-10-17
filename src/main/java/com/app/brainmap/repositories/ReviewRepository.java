package com.app.brainmap.repositories;

import com.app.brainmap.domain.entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    @Query("SELECT AVG(r.rate) FROM Review r WHERE r.mentor.id = :mentorId")
    Double findAverageRatingByMentorId(@Param("mentorId") UUID mentorId);

    long countByMentor_Id(UUID mentorId);
}