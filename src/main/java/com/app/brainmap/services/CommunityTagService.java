package com.app.brainmap.services;

import com.app.brainmap.domain.entities.CommunityTag;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface CommunityTagService {

    List<CommunityTag> getTags();
    List<CommunityTag> createTags(Set<String> tagNames);
    Set<UUID> createTagsForPost(Set<String> tagNames);
    void deleteTag(UUID id);
    CommunityTag getTagById(UUID id);
    List<CommunityTag> getTagsByIds(Set<UUID> ids);
    List<CommunityTag> getPopularTags();
}
