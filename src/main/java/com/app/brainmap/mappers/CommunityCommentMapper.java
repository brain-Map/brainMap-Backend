package com.app.brainmap.mappers;

import com.app.brainmap.domain.dto.CommunityCommentDto;
import com.app.brainmap.domain.dto.CreateCommunityCommentRequestDto;
import com.app.brainmap.domain.dto.UpdateCommunityCommentRequestDto;
import com.app.brainmap.domain.entities.Community.CommunityComment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CommunityCommentMapper {

    @Mapping(target = "communityCommentId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "post", ignore = true)   // set in service
    @Mapping(target = "author", ignore = true) // set in service
    @Mapping(target = "parentComment", ignore = true) // set in service
    @Mapping(target = "replies", ignore = true) // initialize as empty list
    @Mapping(target = "likes", ignore = true) // initialize as empty list
    CommunityComment fromRequest(CreateCommunityCommentRequestDto dto);

    @Mapping(source = "communityCommentId", target = "id")
    @Mapping(source = "post.communityPostId", target = "postId")
    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "author.username", target = "authorName")
    @Mapping(source = "parentComment.communityCommentId", target = "parentCommentId")
    @Mapping(target = "replies", ignore = true) // Handle separately in service for nested structure
    @Mapping(target = "reply", ignore = true) // Set in service based on parentComment
    @Mapping(target = "likesCount", ignore = true) // Will be set in service
    @Mapping(target = "liked", ignore = true) // Will be set in service
    CommunityCommentDto toDto(CommunityComment comment);

    @Mapping(target = "communityCommentId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "post", ignore = true)   // don't change relationships
    @Mapping(target = "author", ignore = true) // don't change relationships
    @Mapping(target = "parentComment", ignore = true) // don't change relationships
    @Mapping(target = "replies", ignore = true) // don't change relationships
    @Mapping(target = "likes", ignore = true) // don't change relationships
    void updateCommentFromDto(UpdateCommunityCommentRequestDto dto, @MappingTarget CommunityComment comment);
}
