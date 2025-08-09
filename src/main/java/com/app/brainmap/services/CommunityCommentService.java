package com.app.brainmap.services;

import com.app.brainmap.domain.dto.CreateCommunityCommentRequestDto;
import com.app.brainmap.domain.dto.CommunityCommentDto;

import java.util.List;
import java.util.UUID;

public interface CommunityCommentService {
    CommunityCommentDto createComment(UUID postId, CreateCommunityCommentRequestDto dto);
    List<CommunityCommentDto> getCommentsByPost(UUID postId);
}