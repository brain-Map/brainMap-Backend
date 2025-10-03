package com.app.brainmap.services.impl;

import com.app.brainmap.domain.dto.CommentLikeResponseDto;
import com.app.brainmap.domain.entities.Community.CommentLike;
import com.app.brainmap.domain.entities.Community.CommunityComment;
import com.app.brainmap.domain.entities.User;
import com.app.brainmap.repositories.CommentLikeRepository;
import com.app.brainmap.repositories.CommunityCommentRepository;
import com.app.brainmap.services.CommentLikeService;
import com.app.brainmap.services.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentLikeServiceImpl implements CommentLikeService {
    
    private final CommentLikeRepository commentLikeRepository;
    private final CommunityCommentRepository communityCommentRepository;
    private final UserService userService;
    
    @Override
    @Transactional
    public CommentLikeResponseDto toggleCommentLike(UUID postId, UUID commentId, UUID userId) {
        log.info("Toggling like for comment {} by user {}", commentId, userId);
        
        // Get the comment
        CommunityComment comment = communityCommentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("Comment not found with id: " + commentId));
        
        // Verify that the comment belongs to the specified post
        // if (!comment.getPost().getCommunityPostId().equals(postId)) {
        //     throw new IllegalArgumentException("Comment does not belong to the specified post");
        // }
        
        // Get the user
        User user = userService.getUserById(userId);
        
        // Check if the user has already liked the comment
        Optional<CommentLike> existingLike = commentLikeRepository.findByCommentAndUser(comment, user);
        
        boolean liked;
        if (existingLike.isPresent()) {
            // Unlike the comment
            commentLikeRepository.delete(existingLike.get());
            liked = false;
            log.info("User {} unliked comment {}", userId, commentId);
        } else {
            // Like the comment
            CommentLike commentLike = CommentLike.builder()
                    .comment(comment)
                    .user(user)
                    .build();
            commentLikeRepository.save(commentLike);
            liked = true;
            log.info("User {} liked comment {}", userId, commentId);
        }
        
        // Get updated like count
        long likesCount = commentLikeRepository.countByCommentId(commentId);
        
        return CommentLikeResponseDto.builder()
                .liked(liked)
                .likesCount(likesCount)
                .build();
    }
    
    @Override
    public long getCommentLikesCount(UUID commentId) {
        return commentLikeRepository.countByCommentId(commentId);
    }
    
    @Override
    public boolean isCommentLikedByUser(UUID commentId, UUID userId) {
        CommunityComment comment = communityCommentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("Comment not found with id: " + commentId));
        
        User user = userService.getUserById(userId);
        
        return commentLikeRepository.existsByCommentAndUser(comment, user);
    }
}
