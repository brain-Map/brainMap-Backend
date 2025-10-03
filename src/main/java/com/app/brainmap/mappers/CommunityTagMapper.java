package com.app.brainmap.mappers;

import com.app.brainmap.domain.dto.CommunityTagResponse;
import com.app.brainmap.domain.entities.CommunityPost;
import com.app.brainmap.domain.entities.CommunityTag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.Set;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommunityTagMapper {

    @Mapping(target = "postCount", source = "posts", qualifiedByName = "calculatePostCount")
    CommunityTagResponse toTagResponse(CommunityTag tag);

    @Named("calculatePostCount")
    default Integer calculatePostCount(Set<CommunityPost> posts){
        if(posts == null){
            return 0;
        }

        return posts.size();
    }
}
