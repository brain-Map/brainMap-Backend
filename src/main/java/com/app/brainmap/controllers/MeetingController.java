package com.app.brainmap.controllers;

import com.app.brainmap.domain.dto.*;
import com.app.brainmap.security.JwtUserDetails;
import com.app.brainmap.services.MeetingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/meetings")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Meeting Management", description = "APIs for managing video meetings with Jitsi Meet")
public class MeetingController {

    private final MeetingService meetingService;

    @Operation(summary = "Create a new meeting", description = "Creates a new video meeting room with a unique room name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Meeting created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MeetingResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Cannot create meeting for another user"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/create")
    public ResponseEntity<MeetingResponseDto> createMeeting(
            @Valid @RequestBody CreateMeetingRequestDto request,
            @AuthenticationPrincipal JwtUserDetails userDetails) {
        
        try {
            log.info("📹 Creating meeting request received - Title: '{}', User: {}", 
                    request.getTitle(), userDetails.getUserId());
            MeetingResponseDto meeting = meetingService.createMeeting(request, userDetails.getUserId());
            log.info("✅ Meeting created successfully - ID: {}, Room: {}", 
                    meeting.getId(), meeting.getRoomName());
            return ResponseEntity.status(HttpStatus.CREATED).body(meeting);
        } catch (Exception e) {
            log.error("❌ Error creating meeting for user {}: {}", 
                    userDetails.getUserId(), e.getMessage(), e);
            throw e;
        }
    }

    @Operation(summary = "Get meeting details", description = "Retrieves meeting details for joining")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Meeting details retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MeetingResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User doesn't have permission to join"),
            @ApiResponse(responseCode = "404", description = "Meeting not found")
    })
    @GetMapping("/{meetingId}")
    public ResponseEntity<MeetingResponseDto> getMeeting(
            @Parameter(description = "Meeting ID", required = true) @PathVariable UUID meetingId,
            @AuthenticationPrincipal JwtUserDetails userDetails) {
        
        log.info("Get meeting request received for meeting: {} by user: {}", meetingId, userDetails.getUserId());
        MeetingResponseDto meeting = meetingService.getMeeting(meetingId, userDetails.getUserId());
        return ResponseEntity.ok(meeting);
    }

    @Operation(summary = "Update meeting participants", description = "Join or leave a meeting (updates participant count)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Participant count updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MeetingResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid action or meeting at capacity"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Meeting not found")
    })
    @PutMapping("/{meetingId}/participants")
    public ResponseEntity<MeetingResponseDto> updateParticipants(
            @Parameter(description = "Meeting ID", required = true) @PathVariable UUID meetingId,
            @Valid @RequestBody UpdateParticipantsRequestDto request,
            @AuthenticationPrincipal JwtUserDetails userDetails) {
        
        log.info("Update participants request received for meeting: {} with action: {} by user: {}", 
                meetingId, request.getAction(), userDetails.getUserId());
        MeetingResponseDto meeting = meetingService.updateParticipants(meetingId, request, userDetails.getUserId());
        return ResponseEntity.ok(meeting);
    }

    @Operation(summary = "End meeting", description = "End an active meeting (only by creator)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Meeting ended successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MeetingResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Only meeting creator can end the meeting"),
            @ApiResponse(responseCode = "404", description = "Meeting not found")
    })
    @PutMapping("/{meetingId}/end")
    public ResponseEntity<MeetingResponseDto> endMeeting(
            @Parameter(description = "Meeting ID", required = true) @PathVariable UUID meetingId,
            @Valid @RequestBody EndMeetingRequestDto request,
            @AuthenticationPrincipal JwtUserDetails userDetails) {
        
        log.info("End meeting request received for meeting: {} by user: {}", meetingId, userDetails.getUserId());
        MeetingResponseDto meeting = meetingService.endMeeting(meetingId, request, userDetails.getUserId());
        return ResponseEntity.ok(meeting);
    }

    @Operation(summary = "Get user's meetings", description = "Retrieve meetings created by a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User meetings retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/users/{userId}")
    public ResponseEntity<Page<MeetingResponseDto>> getUserMeetings(
            @Parameter(description = "User ID", required = true) @PathVariable UUID userId,
            @Parameter(description = "Filter by active status") @RequestParam(required = false) Boolean status,
            @PageableDefault(size = 20) Pageable pageable,
            @AuthenticationPrincipal JwtUserDetails userDetails) {
        
        log.info("Get user meetings request received for user: {} by user: {}", userId, userDetails.getUserId());
        Page<MeetingResponseDto> meetings = meetingService.getUserMeetings(userId, status, pageable);
        return ResponseEntity.ok(meetings);
    }

    @Operation(summary = "Get current user's meetings", description = "Retrieve meetings created by the current authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Current user meetings retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/my-meetings")
    public ResponseEntity<Page<MeetingResponseDto>> getMyMeetings(
            @Parameter(description = "Filter by active status") @RequestParam(required = false) Boolean status,
            @PageableDefault(size = 20) Pageable pageable,
            @AuthenticationPrincipal JwtUserDetails userDetails) {
        
        log.info("Get my meetings request received for user: {}", userDetails.getUserId());
        Page<MeetingResponseDto> meetings = meetingService.getUserMeetings(userDetails.getUserId(), status, pageable);
        return ResponseEntity.ok(meetings);
    }
}
