package com.app.brainmap.services;

import com.app.brainmap.domain.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface MeetingService {

    MeetingResponseDto createMeeting(CreateMeetingRequestDto request, UUID currentUserId);

    MeetingResponseDto getMeeting(UUID meetingId, UUID currentUserId);

    MeetingResponseDto updateParticipants(UUID meetingId, UpdateParticipantsRequestDto request, UUID currentUserId);

    MeetingResponseDto endMeeting(UUID meetingId, EndMeetingRequestDto request, UUID currentUserId);

    Page<MeetingResponseDto> getUserMeetings(UUID userId, Boolean isActive, Pageable pageable);

    void validateMeetingAccess(UUID meetingId, UUID userId);
}
