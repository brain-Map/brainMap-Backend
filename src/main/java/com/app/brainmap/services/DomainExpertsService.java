package com.app.brainmap.services;

import com.app.brainmap.domain.dto.DomainExpert.CompleteDomainExpertProfileDto;
import com.app.brainmap.domain.dto.DomainExpert.DomainExpertProfileDto;
import com.app.brainmap.domain.dto.ServiceListingRequestDto;
import com.app.brainmap.domain.dto.ServiceListingResponseDto;
import com.app.brainmap.domain.entities.DomainExpert.DomainExperts;
import com.app.brainmap.domain.entities.ServiceListing;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface DomainExpertsService {

    List<DomainExperts> listDomainExperts();
    ServiceListing createServiceListing(ServiceListingRequestDto serviceListingRequestDto);
    Page<ServiceListingResponseDto> getAllServiceListings(int page, int size, String sortBy);
    ServiceListingResponseDto getServiceListingById(UUID serviceId);
    ServiceListingResponseDto updateServiceListing(UUID serviceId, ServiceListingRequestDto serviceListingRequestDto);
    void deleteServiceListing(UUID serviceId);
    DomainExpertProfileDto getDomainExpertProfile(UUID userId);
    UUID completeDomainExpertProfile(UUID id, CompleteDomainExpertProfileDto profileDto);
    Boolean isProfileComplete(UUID userId);
}
