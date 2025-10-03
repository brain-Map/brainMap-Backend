package com.app.brainmap.domain.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingResponseDto {

    private UUID id;
    private String roomName;
    private String title;
    private UUID createdBy;
    private LocalDateTime createdAt;
    private Boolean isActive;
    private Integer participantsCount;
    private Integer maxParticipants;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
