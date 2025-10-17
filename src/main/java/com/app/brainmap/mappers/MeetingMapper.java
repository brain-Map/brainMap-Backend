package com.app.brainmap.mappers;

import com.app.brainmap.domain.dto.CreateMeetingRequestDto;
import com.app.brainmap.domain.dto.MeetingResponseDto;
import com.app.brainmap.domain.entities.Meeting;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MeetingMapper {

    MeetingMapper INSTANCE = Mappers.getMapper(MeetingMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roomName", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "participantsCount", ignore = true)
    @Mapping(target = "maxParticipants", ignore = true)
    @Mapping(target = "startTime", ignore = true)
    @Mapping(target = "endTime", ignore = true)
    @Mapping(target = "password", ignore = true)
    Meeting toEntity(CreateMeetingRequestDto dto);

    MeetingResponseDto toDto(Meeting entity);
}
