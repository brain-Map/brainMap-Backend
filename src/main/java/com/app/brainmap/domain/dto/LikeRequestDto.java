package com.app.brainmap.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LikeRequestDto {
    @NotBlank(message = "Target ID is required")
    @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", 
             message = "Target ID must be a valid UUID")
    private String targetId;

    @NotBlank(message = "Target type is required")
    @Pattern(regexp = "^(post|comment)$", message = "Target type must be 'post' or 'comment'")
    private String targetType; // "post" or "comment"

    // Required only for comments/replies (for context)
    @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", 
             message = "Post ID must be a valid UUID")
    private String postId;
}
