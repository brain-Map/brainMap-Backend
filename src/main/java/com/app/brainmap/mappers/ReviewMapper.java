package com.app.brainmap.mappers;

import com.app.brainmap.domain.dto.ReviewDto;
import com.app.brainmap.domain.entities.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReviewMapper {

    @Mapping(target = "memberId", source = "member.id")
    @Mapping(target = "mentorId", source = "mentor.id")
    @Mapping(target = "bookedId", source = "serviceBooking.id")
    ReviewDto toDto(Review review);

    Review toEntity(ReviewDto dto);

}