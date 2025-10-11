package com.app.brainmap.services;

import com.app.brainmap.domain.dto.DomainExpert.ServiceListingRequestDto;
import com.app.brainmap.domain.dto.DomainExpert.ServiceListingResponseDto;
import com.app.brainmap.domain.entities.DomainExpert.ServiceListing;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface ServiceListingService {
    ServiceListing createServiceListing(ServiceListingRequestDto serviceListingRequestDto, UUID mentorId);
    Page<ServiceListingResponseDto> getAllServiceListings(int page, int size, String sortBy);
    ServiceListingResponseDto getServiceListingById(UUID serviceId);
    ServiceListingResponseDto updateServiceListing(UUID serviceId, ServiceListingRequestDto serviceListingRequestDto);
    void deleteServiceListing(UUID serviceId);
}

