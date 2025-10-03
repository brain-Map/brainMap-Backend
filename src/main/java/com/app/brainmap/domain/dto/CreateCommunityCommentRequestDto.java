package com.app.brainmap.domain.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Data
public class CreateCommunityCommentRequestDto {
    @NotBlank(message = "Content cannot be blank")
    @Size(max = 5000, message = "Content cannot exceed 5000 characters")
    private String content; // Comment text
    
    // Optional: if provided, this comment is a reply to the parent comment
    private UUID parentCommentId;
}
