package com.app.brainmap.domain.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
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
    private LocalDateTime updatedAt;
    
    // For nested replies structure
    private UUID parentCommentId; // null for top-level comments
    private List<CommunityCommentDto> replies; // nested replies for hierarchical display
    private boolean reply; // helper field to easily identify replies
    private long likesCount;
    private boolean liked; // Whether current user liked this comment
}
