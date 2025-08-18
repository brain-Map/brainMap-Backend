package com.app.brainmap.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceListingAvailabilityResponseDto {
    private int dayOfWeek; // 1 for Monday, 2 for Tuesday, ..., 7 for Sunday
    private String startTime; // ISO-8601 format (e.g., "10:00:00")
    private String endTime; // ISO-8601 format (e.g., "12:00:00")
}
