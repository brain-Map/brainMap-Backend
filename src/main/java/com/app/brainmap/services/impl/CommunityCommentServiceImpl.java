package com.app.brainmap.services.impl;

import com.app.brainmap.domain.dto.CreateCommunityCommentRequestDto;
import com.app.brainmap.domain.dto.CommunityCommentDto;
import com.app.brainmap.domain.entities.*;
import com.app.brainmap.mappers.CommunityCommentMapper;
import com.app.brainmap.repositories.CommunityCommentRepository;
import com.app.brainmap.repositories.CommunityPostRepository;
import com.app.brainmap.services.CommunityCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommunityCommentServiceImpl implements CommunityCommentService {

    private final CommunityCommentRepository commentRepository;
    private final CommunityPostRepository postRepository;
    private final CommunityCommentMapper mapper;

    @Override
    public CommunityCommentDto createComment(CreateCommunityCommentRequestDto dto) {
        CommunityPost post = postRepository.findById(dto.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found"));

        CommunityComment comment = mapper.fromRequest(dto);
        comment.setPost(post);
        comment.setAuthor(getCurrentUser()); // Your auth logic
        return mapper.toDto(commentRepository.save(comment));
    }

    @Override
    public List<CommunityCommentDto> getCommentsByPost(UUID postId) {
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return commentRepository.findByPost(post)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    private User getCurrentUser() {
        // Fetch from security context or session
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("Demo User");
        return user;
    }
}
