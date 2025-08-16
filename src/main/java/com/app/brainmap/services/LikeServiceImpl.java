package com.app.brainmap.services;

import com.app.brainmap.domain.dto.LikeRequestDto;
import com.app.brainmap.domain.dto.LikeResponseDto;
import com.app.brainmap.domain.entities.CommunityComment;
import com.app.brainmap.domain.entities.CommunityLike;
import com.app.brainmap.domain.entities.CommunityPost;
import com.app.brainmap.domain.entities.User;
import com.app.brainmap.repositories.CommunityCommentRepository;
import com.app.brainmap.repositories.CommunityLikeRepository;
import com.app.brainmap.repositories.CommunityPostRepository;
import com.app.brainmap.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LikeServiceImpl implements LikeService {

    private final CommunityLikeRepository likeRepository;
    private final CommunityPostRepository postRepository;
    private final CommunityCommentRepository commentRepository;
    private final UserRepository userRepository;

    @Override
    public LikeResponseDto toggleLike(LikeRequestDto request, UUID currentUserId) {
        log.info("Toggling like for target: {} of type: {} by user: {}", 
                request.getTargetId(), request.getTargetType(), currentUserId);

        // Validate target type
        if (!"post".equals(request.getTargetType()) && !"comment".equals(request.getTargetType())) {
            throw new IllegalArgumentException("Invalid target type. Must be 'post' or 'comment'");
        }

        // Get the current user
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Convert string UUID to UUID object
        UUID targetId;
        try {
            targetId = UUID.fromString(request.getTargetId());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid target ID format: " + request.getTargetId());
        }

        if ("post".equals(request.getTargetType())) {
            return togglePostLike(targetId, currentUser);
        } else {
            return toggleCommentLike(targetId, currentUser);
        }
    }

    private LikeResponseDto togglePostLike(UUID postId, User currentUser) {
        // Verify post exists
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // Check if like already exists
        Optional<CommunityLike> existingLike = likeRepository.findByAuthorIdAndPostId(currentUser.getId(), postId);

        boolean liked;
        if (existingLike.isPresent()) {
            // Unlike: remove the like
            likeRepository.delete(existingLike.get());
            liked = false;
            log.info("User {} unliked post {}", currentUser.getId(), postId);
        } else {
            // Like: create new like
            CommunityLike newLike = CommunityLike.builder()
                    .author(currentUser)
                    .post(post)
                    .comment(null) // Explicitly null for post likes
                    .build();
            likeRepository.save(newLike);
            liked = true;
            log.info("User {} liked post {}", currentUser.getId(), postId);
        }

        // Get updated likes count
        long likesCount = likeRepository.countByPostId(postId);

        return new LikeResponseDto(liked, likesCount);
    }

    private LikeResponseDto toggleCommentLike(UUID commentId, User currentUser) {
        // Verify comment exists
        CommunityComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        // Check if like already exists
        Optional<CommunityLike> existingLike = likeRepository.findByAuthorIdAndCommentId(currentUser.getId(), commentId);

        boolean liked;
        if (existingLike.isPresent()) {
            // Unlike: remove the like
            likeRepository.delete(existingLike.get());
            liked = false;
            log.info("User {} unliked comment {}", currentUser.getId(), commentId);
        } else {
            // Like: create new like
            CommunityLike newLike = CommunityLike.builder()
                    .author(currentUser)
                    .post(null) // Explicitly null for comment likes
                    .comment(comment)
                    .build();
            likeRepository.save(newLike);
            liked = true;
            log.info("User {} liked comment {}", currentUser.getId(), commentId);
        }

        // Get updated likes count
        long likesCount = likeRepository.countByCommentId(commentId);

        return new LikeResponseDto(liked, likesCount);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isPostLikedByUser(UUID postId, UUID userId) {
        return likeRepository.existsByAuthorIdAndPostId(userId, postId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isCommentLikedByUser(UUID commentId, UUID userId) {
        return likeRepository.existsByAuthorIdAndCommentId(userId, commentId);
    }

    @Override
    @Transactional(readOnly = true)
    public long getPostLikesCount(UUID postId) {
        return likeRepository.countByPostId(postId);
    }

    @Override
    @Transactional(readOnly = true)
    public long getCommentLikesCount(UUID commentId) {
        return likeRepository.countByCommentId(commentId);
    }
}
