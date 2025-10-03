package com.app.brainmap.services.impl;

import com.app.brainmap.domain.dto.LikeRequestDto;
import com.app.brainmap.domain.dto.LikeResponseDto;
import com.app.brainmap.domain.dto.ReviewDto;
import com.app.brainmap.domain.entities.*;
import com.app.brainmap.domain.entities.Community.CommunityComment;
import com.app.brainmap.domain.entities.Community.CommunityLike;
import com.app.brainmap.domain.entities.Community.CommunityPost;
import com.app.brainmap.mappers.ReviewMapper;
import com.app.brainmap.repositories.*;
import com.app.brainmap.services.LikeService;
import com.app.brainmap.services.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final UserRepository userRepository;
    private final PromiseRepository promiseRepository;

    public ReviewServiceImpl(
            ReviewRepository reviewRepository,
            ReviewMapper reviewMapper,
            UserRepository userRepository,
            PromiseRepository promiseRepository
    ) {
        this.reviewRepository = reviewRepository;
        this.reviewMapper = reviewMapper;
        this.userRepository = userRepository;
        this.promiseRepository = promiseRepository;
    }

    @Override
    public List<ReviewDto> listReviews() {
        return reviewRepository.findAll()
                .stream()
                .map(reviewMapper::toDto)
                .toList();
    }

    @Override
    public ReviewDto createReview(ReviewDto reviewDto) {
        Review review = reviewMapper.toEntity(reviewDto);

        User member = userRepository.findById(reviewDto.memberId())
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        User mentor = userRepository.findById(reviewDto.mentorId())
                .orElseThrow(() -> new IllegalArgumentException("Mentor not found"));
        Promise promise = promiseRepository.findById(reviewDto.promiseId())
                .orElseThrow(() -> new IllegalArgumentException("Promise not found"));

        review.setMember(member);
        review.setMentor(mentor);
        review.setPromise(promise);

        Review saved = reviewRepository.save(review);
        return reviewMapper.toDto(saved);
    }

    @Override
    public Optional<ReviewDto> getReview(UUID reviewId) {
        return reviewRepository.findById(reviewId)
                .map(reviewMapper::toDto);
    }

    @Override
    public ReviewDto updateReview(UUID reviewId, ReviewDto reviewDto) {
        Review existing = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));

        existing.setRate(reviewDto.rate());
        existing.setReview(reviewDto.review());

        Review saved = reviewRepository.save(existing);
        return reviewMapper.toDto(saved);
    }

    @Override
    public void deleteReview(UUID reviewId) {
        reviewRepository.deleteById(reviewId);
    }

    @Service
    @RequiredArgsConstructor
    @Slf4j
    @Transactional
    public static class LikeServiceImpl implements LikeService {

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

        // Batch operations for performance optimization
        @Override
        @Transactional(readOnly = true)
        public Map<UUID, Boolean> getPostLikeStatusForUser(List<UUID> postIds, UUID userId) {
            if (postIds.isEmpty()) {
                return new HashMap<>();
            }

            List<UUID> likedPostIds = likeRepository.findLikedPostIdsByAuthorAndPostIds(userId, postIds);

            return postIds.stream().collect(Collectors.toMap(
                postId -> postId,
                likedPostIds::contains
            ));
        }

        @Override
        @Transactional(readOnly = true)
        public Map<UUID, Boolean> getCommentLikeStatusForUser(List<UUID> commentIds, UUID userId) {
            if (commentIds.isEmpty()) {
                return new HashMap<>();
            }

            List<UUID> likedCommentIds = likeRepository.findLikedCommentIdsByAuthorAndCommentIds(userId, commentIds);

            return commentIds.stream().collect(Collectors.toMap(
                commentId -> commentId,
                likedCommentIds::contains
            ));
        }

        @Override
        @Transactional(readOnly = true)
        public Map<UUID, Long> getPostLikeCounts(List<UUID> postIds) {
            if (postIds.isEmpty()) {
                return new HashMap<>();
            }

            List<Object[]> results = likeRepository.countLikesByPostIds(postIds);
            Map<UUID, Long> likeCounts = new HashMap<>();

            // Initialize all post IDs with 0 counts
            postIds.forEach(postId -> likeCounts.put(postId, 0L));

            // Update with actual counts
            results.forEach(result -> {
                UUID postId = (UUID) result[0];
                Long count = (Long) result[1];
                likeCounts.put(postId, count);
            });

            return likeCounts;
        }

        @Override
        @Transactional(readOnly = true)
        public Map<UUID, Long> getCommentLikeCounts(List<UUID> commentIds) {
            if (commentIds.isEmpty()) {
                return new HashMap<>();
            }

            List<Object[]> results = likeRepository.countLikesByCommentIds(commentIds);
            Map<UUID, Long> likeCounts = new HashMap<>();

            // Initialize all comment IDs with 0 counts
            commentIds.forEach(commentId -> likeCounts.put(commentId, 0L));

            // Update with actual counts
            results.forEach(result -> {
                UUID commentId = (UUID) result[0];
                Long count = (Long) result[1];
                likeCounts.put(commentId, count);
            });

            return likeCounts;
        }
    }
}
