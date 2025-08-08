package com.app.brainmap.domain.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class CreateCommunityCommentRequestDto {
    private UUID postId;    // Which post to comment on
    private String content; // Comment text
}
