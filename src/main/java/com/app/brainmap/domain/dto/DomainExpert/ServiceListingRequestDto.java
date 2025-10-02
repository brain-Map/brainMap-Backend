package com.app.brainmap.domain.dto.DomainExpert;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceListingRequestDto {
    private String title;
    private String subject;
    private String description;
    private String pricingType;
    private Double minPrice;
    private Double maxPrice;
    private String serviceType;
    private String mentorshipType;
    private MultipartFile thumbnail; // optional, for image upload
    private List<ServiceListingAvailabilityRequestDto> availabilities;
    private List<WhatYouGetDto> whatYouGet;
}
