package com.app.brainmap.mappers;


import com.app.brainmap.domain.CommunityPostType;
import com.app.brainmap.domain.CreateCommunityPostRequest;
import com.app.brainmap.domain.dto.CommunityPostAuthorDto;
import com.app.brainmap.domain.dto.CommunityPostDto;
import com.app.brainmap.domain.dto.CommunityTagResponse;
import com.app.brainmap.domain.dto.CreateCommunityPostRequestDto;
import com.app.brainmap.domain.entities.CommunityPost;
import com.app.brainmap.domain.entities.CommunityTag;
import com.app.brainmap.domain.entities.User;
import com.app.brainmap.services.CommunityTagService;
import org.mapstruct.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommunityPostMapper {

    @Mapping(target = "communityPostId", source = "communityPostId")
    @Mapping(target = "tags", source = "tags", qualifiedByName = "tagsToCommunityTagResponse")
    @Mapping(target = "author", source = "author", qualifiedByName = "userToAuthorDto")
    CommunityPostDto toDto(CommunityPost post);

    @Mapping(target = "type", source = "type", qualifiedByName = "stringToCommunityPostType")
    @Mapping(target = "tagsIds", expression = "java(tagsToIds(dto.getTags(), communityTagService))")
    CreateCommunityPostRequest toCreateCommunityPostRequest(CreateCommunityPostRequestDto dto, @Context CommunityTagService communityTagService);

    @Named("stringToCommunityPostType")
    default CommunityPostType stringToCommunityPostType(String type) {
        if (type == null) {
            return null;
        }
        try {
            return CommunityPostType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid post type: " + type);
        }
    }

    default Set<UUID> tagsToIds(Set<String> tags, @Context CommunityTagService communityTagService) {
        if (tags == null || tags.isEmpty()) {
            return new HashSet<>();
        }
        return communityTagService.createTagsForPost(tags);
    }

    @Named("userToAuthorDto")
    default CommunityPostAuthorDto userToAuthorDto(User user) {
        if (user == null) return null;
        return CommunityPostAuthorDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .build();
    }

    @Named("tagsToCommunityTagResponse")
    default CommunityTagResponse tagsToCommunityTagResponse(CommunityTag tag){
        if (tag == null) return null;
        return CommunityTagResponse.builder()
                .id(tag.getTagId())
                .name(tag.getName())
                .build();
    }
}
