package com.app.brainmap.domain.dto.DomainExpert;

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
    private UUID serviceId;
    private String title;
    private String subject;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID mentorId;
    private List<ServiceListingAvailabilityResponseDto> availabilities;
    private String thumbnailUrl;
    private Integer duration;
    private String mentorFirstName;
    private String mentorLastName;
    private String mentorshipType;
    private String mentorBio;
    private String mentorAvatar;
    private Double hourlyRatePerPerson;
    private Double hourlyRatePerGroup;
    private List<String> expertiseAreas;
    private List<WhatYouGetDto> whatYouGet;
    }
