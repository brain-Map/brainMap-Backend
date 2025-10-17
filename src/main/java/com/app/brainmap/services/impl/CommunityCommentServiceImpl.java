package com.app.brainmap.services.impl;

import com.app.brainmap.domain.dto.CreateCommunityCommentRequestDto;
import com.app.brainmap.domain.dto.CommunityCommentDto;
import com.app.brainmap.domain.dto.TopCommenterDto;
import com.app.brainmap.domain.UserRoleType;
import com.app.brainmap.domain.entities.*;
import com.app.brainmap.domain.entities.Community.CommunityComment;
import com.app.brainmap.domain.entities.Community.CommunityPost;
import com.app.brainmap.mappers.CommunityCommentMapper;
import com.app.brainmap.repositories.CommunityCommentRepository;
import com.app.brainmap.repositories.CommunityPostRepository;
import com.app.brainmap.services.CommunityCommentService;
import com.app.brainmap.services.LikeService;
import com.app.brainmap.services.UserService;
import com.app.brainmap.security.JwtUserDetails;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommunityCommentServiceImpl implements CommunityCommentService {

    private final CommunityCommentRepository commentRepository;
    private final CommunityPostRepository postRepository;
    private final CommunityCommentMapper mapper;
    private final UserService userService;
    private final LikeService likeService;

    @Override
    @Transactional
    public CommunityCommentDto createComment(UUID postId, CreateCommunityCommentRequestDto dto) {
        log.info("Creating comment for post: {}, parentCommentId: {}", postId, dto.getParentCommentId());
        
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post not found with id: " + postId));

        User currentUser = getCurrentUser();
        log.info("Comment being created by user: {}", currentUser.getId());

        CommunityComment comment = mapper.fromRequest(dto);
        comment.setPost(post);
        comment.setAuthor(currentUser);
        
        // Handle parent comment for replies
        if (dto.getParentCommentId() != null) {
            CommunityComment parentComment = commentRepository.findById(dto.getParentCommentId())
                    .orElseThrow(() -> new NoSuchElementException("Parent comment not found with id: " + dto.getParentCommentId()));
            
            // Ensure the parent comment belongs to the same post
            if (!parentComment.getPost().getCommunityPostId().equals(postId)) {
                throw new IllegalArgumentException("Parent comment does not belong to the specified post");
            }
            
            comment.setParentComment(parentComment);
            log.info("Creating reply to comment: {}", dto.getParentCommentId());
        } else {
            log.info("Creating top-level comment");
        }
        
        CommunityComment savedComment = commentRepository.save(comment);
        log.info("Comment created successfully with id: {}", savedComment.getCommunityCommentId());
        
        CommunityCommentDto resultDto = mapper.toDto(savedComment);
        resultDto.setReply(savedComment.isReply());
        
        // Set like information for the new comment
        resultDto.setLikesCount(0); // New comments start with 0 likes
        resultDto.setLiked(false);  // User hasn't liked their own comment yet
        
        return resultDto;
    }

    @Override
    public List<CommunityCommentDto> getCommentsByPost(UUID postId) {
        log.info("Fetching comments for post: {}", postId);
        
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post not found with id: " + postId));
        
        // Get only top-level comments first
        List<CommunityComment> topLevelComments = commentRepository.findByPostAndParentCommentIsNull(post);
        log.info("Found {} top-level comments for post: {}", topLevelComments.size(), postId);
        
        // Convert to DTOs and populate replies recursively with batch optimization
        List<CommunityCommentDto> commentDtos = convertCommentsToHierarchicalDtos(topLevelComments);
        
        return commentDtos;
    }
    
    @Override
    public List<TopCommenterDto> getTopCommentersByPost(UUID postId) {
        log.info("Fetching top commenters for post: {}", postId);
        
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post not found with id: " + postId));
        
        List<Object[]> results = commentRepository.findTopCommentersByPost(post);
        
        List<TopCommenterDto> topCommenters = new ArrayList<>();
        for (Object[] result : results) {
            UUID userId = (UUID) result[0];
            String firstName = (String) result[1];
            String lastName = (String) result[2];
            String username = (String) result[3];
            UserRoleType userRole = (UserRoleType) result[4];
            Long commentCount = (Long) result[5];
            
            // Create display name (prefer firstName + lastName, fallback to username)
            String displayName = "";
            if (firstName != null && lastName != null) {
                displayName = firstName + " " + lastName;
            } else if (firstName != null) {
                displayName = firstName;
            } else if (username != null) {
                displayName = username;
            } else {
                displayName = "Anonymous User";
            }
            
            // Convert UserRoleType to display string
            String roleDisplay = userRole != null ? formatRole(userRole) : null;
            
            TopCommenterDto commenter = TopCommenterDto.builder()
                    .id(userId)
                    .name(displayName)
                    .avatar(null) // Avatar URL can be added later if needed
                    .commentCount(commentCount)
                    .role(roleDisplay)
                    .build();
            
            topCommenters.add(commenter);
        }
        
        log.info("Found {} top commenters for post: {}", topCommenters.size(), postId);
        return topCommenters;
    }
    
    private String formatRole(UserRoleType role) {
        switch (role) {
            case PROJECT_MEMBER:
                return "Project Member";
            case MENTOR:
                return "Mentor";
            case MODERATOR:
                return "Moderator";
            case ADMIN:
                return "Admin";
            default:
                return role.name();
        }
    }
    
    private List<CommunityCommentDto> convertCommentsToHierarchicalDtos(List<CommunityComment> comments) {
        if (comments.isEmpty()) {
            return new ArrayList<>();
        }
        
        User currentUser = getCurrentUser();
        
        // Collect all comment IDs for batch processing (including nested replies)
        List<UUID> allCommentIds = collectAllCommentIds(comments);
        
        // Batch fetch like counts and statuses
        Map<UUID, Long> likeCounts = likeService.getCommentLikeCounts(allCommentIds);
        Map<UUID, Boolean> likeStatuses = likeService.getCommentLikeStatusForUser(allCommentIds, currentUser.getId());
        
        return comments.stream()
                .map(comment -> convertToHierarchicalDtoWithBatch(comment, likeCounts, likeStatuses))
                .toList();
    }
    
    private List<UUID> collectAllCommentIds(List<CommunityComment> comments) {
        List<UUID> allIds = new ArrayList<>();
        for (CommunityComment comment : comments) {
            allIds.add(comment.getCommunityCommentId());
            // Recursively collect reply IDs
            List<CommunityComment> replies = commentRepository.findByParentComment(comment);
            if (!replies.isEmpty()) {
                allIds.addAll(collectAllCommentIds(replies));
            }
        }
        return allIds;
    }
    
    private CommunityCommentDto convertToHierarchicalDtoWithBatch(
            CommunityComment comment, 
            Map<UUID, Long> likeCounts, 
            Map<UUID, Boolean> likeStatuses) {
        
        CommunityCommentDto dto = mapper.toDto(comment);
        
        // Set the isReply field manually
        dto.setReply(comment.isReply());
        
        // Set like information using batch data
        UUID commentId = comment.getCommunityCommentId();
        dto.setLikesCount(likeCounts.getOrDefault(commentId, 0L));
        dto.setLiked(likeStatuses.getOrDefault(commentId, false));
        
        // Get and convert replies recursively using the same batch data
        List<CommunityComment> replies = commentRepository.findByParentComment(comment);
        if (!replies.isEmpty()) {
            List<CommunityCommentDto> replyDtos = replies.stream()
                    .map(reply -> convertToHierarchicalDtoWithBatch(reply, likeCounts, likeStatuses))
                    .toList();
            dto.setReplies(replyDtos);
        }
        
        return dto;
    }

    @Override
    @Transactional
    public void deleteComment(UUID postId, UUID commentId) {
        log.info("Attempting to delete comment: {} from post: {}", commentId, postId);
        
        // 1. Find the comment
        CommunityComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    log.error("Comment not found with id: {}", commentId);
                    return new NoSuchElementException("Comment not found");
                });
        
        // Force lazy loading of post and author to avoid LazyInitializationException
        CommunityPost post = comment.getPost();
        User author = comment.getAuthor();
        UUID postIdFromComment = post.getCommunityPostId();
        UUID authorId = author.getId();
        
        // 2. Validate that the comment belongs to the specified post
        if (!postIdFromComment.equals(postId)) {
            log.error("Comment {} does not belong to post {}", commentId, postId);
            throw new IllegalArgumentException("Comment does not belong to this post");
        }
        
        // 3. Verify ownership - user can only delete their own comments
        User currentUser = getCurrentUser();
        if (!authorId.equals(currentUser.getId())) {
            log.error("User {} attempted to delete comment {} owned by user {}", 
                    currentUser.getId(), commentId, authorId);
            throw new SecurityException("You can only delete your own comments");
        }
        
        log.info("User {} authorized to delete comment {}", currentUser.getId(), commentId);
        
        // 4. Count total comments to be deleted (including nested replies)
        int totalDeleted = countCommentsRecursively(comment);
        log.info("Will delete {} comment(s) including replies", totalDeleted);
        
        // 5. Cascade delete: Delete the comment and all its replies (handled by JPA cascade)
        // The @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true) in CommunityComment
        // will automatically delete all child comments
        deleteCommentAndRepliesRecursively(comment);
        
        log.info("Successfully deleted comment {} and {} replies", commentId, totalDeleted - 1);
    }
    
    /**
     * Recursively count all comments including nested replies
     */
    private int countCommentsRecursively(CommunityComment comment) {
        int count = 1; // Count the comment itself
        List<CommunityComment> replies = commentRepository.findByParentCommentCommunityCommentId(comment.getCommunityCommentId());
        for (CommunityComment reply : replies) {
            count += countCommentsRecursively(reply);
        }
        return count;
    }
    
    /**
     * Recursively delete comment and all its replies
     * This will also delete all associated likes due to cascade settings
     */
    private void deleteCommentAndRepliesRecursively(CommunityComment comment) {
        UUID commentId = comment.getCommunityCommentId();
        
        // 1. Find all direct replies
        List<CommunityComment> replies = commentRepository.findByParentCommentCommunityCommentId(commentId);
        
        // 2. Recursively delete all replies first
        for (CommunityComment reply : replies) {
            deleteCommentAndRepliesRecursively(reply);
        }
        
        // 3. Delete the comment itself
        // The cascade settings on CommunityComment.likes will automatically delete associated likes
        log.info("Deleting comment: {}", commentId);
        commentRepository.delete(comment);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails userDetails = (authentication != null && authentication.getPrincipal() != null)
                ? authentication.getPrincipal() instanceof JwtUserDetails
                ? (JwtUserDetails) authentication.getPrincipal()
                : null
                : null;

        if (userDetails == null) {
            // For development: Get the first available user from database
            log.warn("No authentication found, using first available user from database for development");
            List<User> users = userService.getAllUsers();
            
            if (users.isEmpty()) {
                throw new IllegalStateException("No users found in database. Please create a user first.");
            }
            
            User firstUser = users.get(0);
            log.info("Using user from database: {} ({})", firstUser.getUsername(), firstUser.getId());
            return firstUser;
        }

        UUID userId = userDetails.getUserId();
        return userService.getUserById(userId);
    }
}
