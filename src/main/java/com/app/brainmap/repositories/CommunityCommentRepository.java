package com.app.brainmap.repositories;

import com.app.brainmap.domain.entities.CommunityComment;
import com.app.brainmap.domain.entities.CommunityPost;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;

@Repository
public interface CommunityCommentRepository extends JpaRepository<CommunityComment, UUID> {
    List<CommunityComment> findByPost(CommunityPost post);
    
    // Find only top-level comments (parentComment is null)
    List<CommunityComment> findByPostAndParentCommentIsNull(CommunityPost post);
    
    // Find replies to a specific comment
    List<CommunityComment> findByParentComment(CommunityComment parentComment);
    
    // Find all comments and replies for a post with proper ordering
    @Query("SELECT c FROM CommunityComment c WHERE c.post = :post ORDER BY c.parentComment.communityCommentId ASC NULLS FIRST, c.createdAt ASC")
    List<CommunityComment> findByPostOrderedHierarchically(@Param("post") CommunityPost post);
}
