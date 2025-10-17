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
    private String category;
    private String description;
    private List<String> availabilityModes;
    private List<WhatYouGetDto> whatYouGet;
    private List<ServiceListingPricingRequestDto> pricings;
    private MultipartFile thumbnail; // optional, for image upload
}
