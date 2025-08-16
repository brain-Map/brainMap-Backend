package com.app.brainmap.repositories;

import com.app.brainmap.domain.entities.CommunityLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommunityLikeRepository extends JpaRepository<CommunityLike, UUID> {
    
    // Find like for a post by a specific user
    @Query("SELECT cl FROM CommunityLike cl WHERE cl.author.id = :authorId AND cl.post.id = :postId")
    Optional<CommunityLike> findByAuthorIdAndPostId(@Param("authorId") UUID authorId, @Param("postId") UUID postId);
    
    // Find like for a comment by a specific user
    @Query("SELECT cl FROM CommunityLike cl WHERE cl.author.id = :authorId AND cl.comment.id = :commentId")
    Optional<CommunityLike> findByAuthorIdAndCommentId(@Param("authorId") UUID authorId, @Param("commentId") UUID commentId);
    
    // Count total likes for a post
    @Query("SELECT COUNT(cl) FROM CommunityLike cl WHERE cl.post.id = :postId")
    long countByPostId(@Param("postId") UUID postId);
    
    // Count total likes for a comment
    @Query("SELECT COUNT(cl) FROM CommunityLike cl WHERE cl.comment.id = :commentId")
    long countByCommentId(@Param("commentId") UUID commentId);
    
    // Check if user liked a post
    @Query("SELECT CASE WHEN COUNT(cl) > 0 THEN true ELSE false END FROM CommunityLike cl WHERE cl.author.id = :authorId AND cl.post.id = :postId")
    boolean existsByAuthorIdAndPostId(@Param("authorId") UUID authorId, @Param("postId") UUID postId);
    
    // Check if user liked a comment
    @Query("SELECT CASE WHEN COUNT(cl) > 0 THEN true ELSE false END FROM CommunityLike cl WHERE cl.author.id = :authorId AND cl.comment.id = :commentId")
    boolean existsByAuthorIdAndCommentId(@Param("authorId") UUID authorId, @Param("commentId") UUID commentId);
}
