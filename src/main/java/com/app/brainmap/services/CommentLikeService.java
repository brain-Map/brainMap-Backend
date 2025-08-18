package com.app.brainmap.services;

import com.app.brainmap.domain.dto.CommentLikeResponseDto;

import java.util.UUID;

public interface CommentLikeService {
    CommentLikeResponseDto toggleCommentLike(UUID postId, UUID commentId, UUID userId);
    long getCommentLikesCount(UUID commentId);
    boolean isCommentLikedByUser(UUID commentId, UUID userId);
}
