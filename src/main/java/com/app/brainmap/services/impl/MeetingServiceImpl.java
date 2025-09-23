package com.app.brainmap.services.impl;

import com.app.brainmap.domain.dto.*;
import com.app.brainmap.domain.entities.Meeting;
import com.app.brainmap.mappers.MeetingMapper;
import com.app.brainmap.repositories.MeetingRepository;
import com.app.brainmap.repositories.UserRepository;
import com.app.brainmap.services.MeetingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MeetingServiceImpl implements MeetingService {

    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;
    private final MeetingMapper meetingMapper = MeetingMapper.INSTANCE;

    @Override
    public MeetingResponseDto createMeeting(CreateMeetingRequestDto request, UUID currentUserId) {
        log.info("Creating meeting with title: {} for user: {}", request.getTitle(), currentUserId);

        // Validate user exists
        if (!userRepository.existsById(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        // Generate unique room name
        String roomName = generateUniqueRoomName();

        // Create meeting entity
        Meeting meeting = meetingMapper.toEntity(request);
        meeting.setId(UUID.randomUUID());
        meeting.setRoomName(roomName);
        meeting.setCreatedBy(currentUserId); // Set the createdBy from the JWT token
        meeting.setCreatedAt(LocalDateTime.now());
        meeting.setUpdatedAt(LocalDateTime.now());
        meeting.setIsActive(true);
        meeting.setParticipantsCount(0);
        meeting.setMaxParticipants(50);

        // Save meeting
        Meeting savedMeeting = meetingRepository.save(meeting);

        log.info("Meeting created successfully with ID: {} and room name: {}", savedMeeting.getId(), roomName);

        return meetingMapper.toDto(savedMeeting);
    }

    @Override
    @Transactional(readOnly = true)
    public MeetingResponseDto getMeeting(UUID meetingId, UUID currentUserId) {
        log.info("Fetching meeting with ID: {} for user: {}", meetingId, currentUserId);

        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Meeting not found"));

        // For now, allow any authenticated user to join any meeting
        // In the future, you can add more specific access control logic here
        
        return meetingMapper.toDto(meeting);
    }

    @Override
    public MeetingResponseDto updateParticipants(UUID meetingId, UpdateParticipantsRequestDto request, UUID currentUserId) {
        log.info("Updating participants for meeting: {} with action: {} by user: {}", meetingId, request.getAction(), currentUserId);

        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Meeting not found"));

        if (!meeting.getIsActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot update participants for inactive meeting");
        }

        // Validate action
        if (!"join".equals(request.getAction()) && !"leave".equals(request.getAction())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid action. Must be 'join' or 'leave'");
        }

        // Update participant count
        int currentCount = meeting.getParticipantsCount();
        if ("join".equals(request.getAction())) {
            if (currentCount >= meeting.getMaxParticipants()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Meeting is at maximum capacity");
            }
            meeting.setParticipantsCount(currentCount + 1);
        } else if ("leave".equals(request.getAction())) {
            if (currentCount > 0) {
                meeting.setParticipantsCount(currentCount - 1);
            }
        }

        Meeting savedMeeting = meetingRepository.save(meeting);
        return meetingMapper.toDto(savedMeeting);
    }

    @Override
    public MeetingResponseDto endMeeting(UUID meetingId, EndMeetingRequestDto request, UUID currentUserId) {
        log.info("Ending meeting: {} by user: {}", meetingId, currentUserId);

        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Meeting not found"));

        // Only the creator can end the meeting
        if (!meeting.getCreatedBy().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the meeting creator can end the meeting");
        }

        meeting.setIsActive(false);
        meeting.setEndTime(LocalDateTime.now());
        meeting.setParticipantsCount(0);

        Meeting savedMeeting = meetingRepository.save(meeting);
        return meetingMapper.toDto(savedMeeting);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MeetingResponseDto> getUserMeetings(UUID userId, Boolean isActive, Pageable pageable) {
        log.info("Fetching meetings for user: {} with active status: {}", userId, isActive);

        Page<Meeting> meetings;
        if (isActive != null) {
            meetings = meetingRepository.findByCreatedByAndIsActiveOrderByCreatedAtDesc(userId, isActive, pageable);
        } else {
            meetings = meetingRepository.findByCreatedByOrderByCreatedAtDesc(userId, pageable);
        }

        return meetings.map(meetingMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public void validateMeetingAccess(UUID meetingId, UUID userId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Meeting not found"));

        if (!meeting.getIsActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Meeting is not active");
        }

        // For now, allow any authenticated user to access any active meeting
        // In the future, you can add more specific access control logic here
    }

    private String generateUniqueRoomName() {
        String roomName;
        int attempts = 0;
        final int maxAttempts = 10;

        do {
            String prefix = "brainmap";
            String randomPart = UUID.randomUUID().toString()
                    .replace("-", "")
                    .substring(0, 12);
            roomName = prefix + "-" + randomPart;
            attempts++;

            if (attempts >= maxAttempts) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                    "Failed to generate unique room name after " + maxAttempts + " attempts");
            }
        } while (meetingRepository.existsByRoomName(roomName));

        return roomName;
    }
}
