package com.app.brainmap.repositories;

import com.app.brainmap.domain.entities.Community.CommunityPost;
import com.app.brainmap.domain.entities.Community.CommunityTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommunityPostRepository extends JpaRepository<CommunityPost, UUID> {
    List<CommunityPost> findAllByTags(CommunityTag tag);
    List<CommunityPost> findAllByAuthor_Id(UUID authorId);
}
