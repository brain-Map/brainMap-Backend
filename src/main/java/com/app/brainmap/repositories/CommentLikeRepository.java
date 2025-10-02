package com.app.brainmap.repositories;

import com.app.brainmap.domain.entities.Community.CommentLike;
import com.app.brainmap.domain.entities.Community.CommunityComment;
import com.app.brainmap.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, UUID> {
    
    Optional<CommentLike> findByCommentAndUser(CommunityComment comment, User user);
    
    @Query("SELECT COUNT(cl) FROM CommentLike cl WHERE cl.comment.communityCommentId = :commentId")
    long countByCommentId(@Param("commentId") UUID commentId);
    
    boolean existsByCommentAndUser(CommunityComment comment, User user);
}
