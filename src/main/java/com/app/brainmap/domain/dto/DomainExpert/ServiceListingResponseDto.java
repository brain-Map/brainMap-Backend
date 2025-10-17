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
    private String category;
    private String description;
    private java.util.List<String> availabilityModes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID mentorId;
    private String thumbnailUrl;
    private String mentorFirstName;
    private String mentorLastName;
    private String mentorBio;
    private String mentorAvatar;
    private List<ServiceListingPricingResponseDto> pricings;
    private List<String> expertiseAreas;
    private List<WhatYouGetDto> whatYouGet;
    }
