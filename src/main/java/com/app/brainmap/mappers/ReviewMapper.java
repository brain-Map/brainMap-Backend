package com.app.brainmap.mappers;

import com.app.brainmap.domain.dto.ReviewDto;
import com.app.brainmap.domain.entities.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReviewMapper {
    @Mapping(source = "member.userId", target = "memberId")
    @Mapping(source = "mentor.userId", target = "mentorId")
    @Mapping(source = "promise.promiseId", target = "promiseId")
    ReviewDto toDto(Review review);

    @Mapping(target = "member", ignore = true) // To be set manually in the service layer
    @Mapping(target = "mentor", ignore = true) // To be set manually in the service layer
    @Mapping(target = "promise", ignore = true) // To be set manually in the service layer
    Review toEntity(ReviewDto dto);
}
