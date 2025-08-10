package com.app.brainmap.mappers;

import com.app.brainmap.domain.dto.ReviewDto;
import com.app.brainmap.domain.entities.Review;
import com.app.brainmap.domain.entities.User;
import com.app.brainmap.domain.entities.Promise;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReviewMapper {
    @Mapping(source = "member", target = "memberId", qualifiedByName = "mapUserToUserId")
    @Mapping(source = "mentor", target = "mentorId", qualifiedByName = "mapUserToUserId")
    @Mapping(source = "promise", target = "promiseId", qualifiedByName = "mapPromiseToPromiseId")
    ReviewDto toDto(Review review);

    @Mapping(target = "member", ignore = true)
    @Mapping(target = "mentor", ignore = true)
    @Mapping(target = "promise", ignore = true)
    Review toEntity(ReviewDto dto);

    @Named("mapUserToUserId")
    default UUID mapUserToUserId(User user) {
        return user != null ? (UUID) user.getUserId() : null;
    }

    @Named("mapPromiseToPromiseId")
    default UUID mapPromiseToPromiseId(Promise promise) {
        return promise != null ? promise.getPromiseId() : null;
    }
}