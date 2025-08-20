package com.app.brainmap.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceListingResponseDto {
    private UUID ServiceId;
    private String title;
    private String subject;
    private String description;
    private Double fee;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID mentorId;
    private List<ServiceListingAvailabilityResponseDto> availabilities;
    }
