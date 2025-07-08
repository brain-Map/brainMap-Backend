package com.app.brainmap.mappers;


import com.app.brainmap.domain.CreateCommunityPostRequest;
import com.app.brainmap.domain.dto.CommunityPostDto;
import com.app.brainmap.domain.dto.CreateCommunityPostRequestDto;
import com.app.brainmap.domain.entities.CommunityPost;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommunityPostMapper {

    @Mapping(target = "author", source = "author")
    @Mapping(target = "tags", source = "tags")
    CommunityPostDto toDto(CommunityPost post);

    CreateCommunityPostRequest toCreateCommunityPostRequest(CreateCommunityPostRequestDto dto);
}
