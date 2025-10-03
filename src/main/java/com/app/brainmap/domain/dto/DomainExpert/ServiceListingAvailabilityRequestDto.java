package com.app.brainmap.domain.dto.DomainExpert;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceListingAvailabilityRequestDto {
    private int dayOfWeek; // 1 for Monday, 2 for Tuesday, ..., 7 for Sunday
    private LocalTime startTime;
    private LocalTime endTime;
}
