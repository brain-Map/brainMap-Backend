package com.app.brainmap.services;

import com.app.brainmap.domain.dto.LikeRequestDto;
import com.app.brainmap.domain.dto.LikeResponseDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface LikeService {
    LikeResponseDto toggleLike(LikeRequestDto request, UUID currentUserId);
    boolean isPostLikedByUser(UUID postId, UUID userId);
    boolean isCommentLikedByUser(UUID commentId, UUID userId);
    long getPostLikesCount(UUID postId);
    long getCommentLikesCount(UUID commentId);
    
    // Batch operations for performance optimization
    Map<UUID, Boolean> getPostLikeStatusForUser(List<UUID> postIds, UUID userId);
    Map<UUID, Boolean> getCommentLikeStatusForUser(List<UUID> commentIds, UUID userId);
    Map<UUID, Long> getPostLikeCounts(List<UUID> postIds);
    Map<UUID, Long> getCommentLikeCounts(List<UUID> commentIds);
}
