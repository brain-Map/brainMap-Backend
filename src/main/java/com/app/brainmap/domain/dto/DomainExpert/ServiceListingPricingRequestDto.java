package com.app.brainmap.domain.dto.DomainExpert;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceListingPricingRequestDto {
    private String pricingType; // hourly, monthly, project-based, yearly
    private Double price;
}

