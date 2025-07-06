package com.app.brainmap.repositories;

import com.app.brainmap.domain.entities.CommunityComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CommunityCommentRepository extends JpaRepository<CommunityComment, UUID> {
}
