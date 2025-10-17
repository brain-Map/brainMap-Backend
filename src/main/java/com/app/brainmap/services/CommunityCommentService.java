package com.app.brainmap.services;

import com.app.brainmap.domain.dto.CreateCommunityCommentRequestDto;
import com.app.brainmap.domain.dto.CommunityCommentDto;
import com.app.brainmap.domain.dto.TopCommenterDto;

import java.util.List;
import java.util.UUID;

public interface CommunityCommentService {
    CommunityCommentDto createComment(UUID postId, CreateCommunityCommentRequestDto dto);
    List<CommunityCommentDto> getCommentsByPost(UUID postId);
    List<TopCommenterDto> getTopCommentersByPost(UUID postId);
    void deleteComment(UUID postId, UUID commentId);
}