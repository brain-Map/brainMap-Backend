package com.app.brainmap.domain.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
// This DTO represents a community comment, including its ID, content, author information, and creation
public class CommunityCommentDto {
    private UUID id;
    private String content;
    private UUID postId; // ID of the post this comment belongs to
    private UUID authorId;
    private String authorName; // optional for displaying
    private LocalDateTime createdAt;
}
