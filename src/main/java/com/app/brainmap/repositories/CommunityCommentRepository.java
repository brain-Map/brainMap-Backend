package com.app.brainmap.repositories;

import com.app.brainmap.domain.entities.CommunityComment;
import com.app.brainmap.domain.entities.CommunityPost;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;

@Repository
public interface CommunityCommentRepository extends JpaRepository<CommunityComment, UUID> {
    List<CommunityComment> findByPost(CommunityPost post);
}
