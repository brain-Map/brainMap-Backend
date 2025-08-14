package com.app.brainmap.services.impl;

import com.app.brainmap.domain.dto.CreateCommunityCommentRequestDto;
import com.app.brainmap.domain.dto.CommunityCommentDto;
import com.app.brainmap.domain.entities.*;
import com.app.brainmap.mappers.CommunityCommentMapper;
import com.app.brainmap.repositories.CommunityCommentRepository;
import com.app.brainmap.repositories.CommunityPostRepository;
import com.app.brainmap.services.CommunityCommentService;
import com.app.brainmap.services.UserService;
import com.app.brainmap.security.JwtUserDetails;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
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

    @Override
    @Transactional
    public CommunityCommentDto createComment(UUID postId, CreateCommunityCommentRequestDto dto) {
        log.info("Creating comment for post: {}", postId);
        
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post not found with id: " + postId));

        User currentUser = getCurrentUser();
        log.info("Comment being created by user: {}", currentUser.getId());

        CommunityComment comment = mapper.fromRequest(dto);
        comment.setPost(post);  // Set the actual CommunityPost entity
        comment.setAuthor(currentUser);
        
        CommunityComment savedComment = commentRepository.save(comment);
        log.info("Comment created successfully with id: {}", savedComment.getCommunityCommentId());
        
        return mapper.toDto(savedComment);
    }

    @Override
    public List<CommunityCommentDto> getCommentsByPost(UUID postId) {
        log.info("Fetching comments for post: {}", postId);
        
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post not found with id: " + postId));
        
        List<CommunityComment> comments = commentRepository.findByPost(post);
        log.info("Found {} comments for post: {}", comments.size(), postId);
        
        return comments.stream()
                .map(mapper::toDto)
                .toList();
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
