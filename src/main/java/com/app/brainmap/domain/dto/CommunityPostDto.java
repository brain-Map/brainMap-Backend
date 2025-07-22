package com.app.brainmap.domain.dto;

import com.app.brainmap.domain.CommunityPostType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommunityPostDto {
    private UUID communityPostId;
    private String title;
    private String content;
    private Set<CommunityTagResponse> tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private CommunityPostAuthorDto author;
    private CommunityPostType type;
}
