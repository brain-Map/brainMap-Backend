package com.app.brainmap.domain;

import com.app.brainmap.domain.entities.Community.CommunityPostType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateCommunityPostRequest {
    private String title;
    private String content;
    private CommunityPostType type;

    @Builder.Default
    private Set<UUID> tagsIds = new HashSet<>();
}
