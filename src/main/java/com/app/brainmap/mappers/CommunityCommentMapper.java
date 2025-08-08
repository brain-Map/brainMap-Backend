package com.app.brainmap.mappers;

import com.app.brainmap.domain.dto.CommunityCommentDto;
import com.app.brainmap.domain.dto.CreateCommunityCommentRequestDto;
import com.app.brainmap.domain.entities.CommunityComment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommunityCommentMapper {

    @Mapping(target = "communityCommentId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "post", ignore = true)   // set in service
    @Mapping(target = "author", ignore = true) // set in service
    CommunityComment fromRequest(CreateCommunityCommentRequestDto dto);

    @Mapping(source = "communityCommentId", target = "id")
    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "author.username", target = "authorName") // adjust field names
    CommunityCommentDto toDto(CommunityComment comment);
}
