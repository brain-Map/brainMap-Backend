package com.app.brainmap.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceListingRequestDto {
    private String title;
    private String subject;
    private String description;
    private Double fee;
    private UUID mentorId;
    private List<ServiceListingAvailabilityRequestDto> availabilities;
}
