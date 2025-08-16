package com.app.brainmap.services;

import com.app.brainmap.domain.dto.LikeRequestDto;
import com.app.brainmap.domain.dto.LikeResponseDto;

import java.util.UUID;

public interface LikeService {
    LikeResponseDto toggleLike(LikeRequestDto request, UUID currentUserId);
    boolean isPostLikedByUser(UUID postId, UUID userId);
    boolean isCommentLikedByUser(UUID commentId, UUID userId);
    long getPostLikesCount(UUID postId);
    long getCommentLikesCount(UUID commentId);
}
