package com.app.brainmap.services;

import com.app.brainmap.domain.dto.ServiceListingRequestDto;
import com.app.brainmap.domain.dto.ServiceListingResponseDto;
import com.app.brainmap.domain.entities.DomainExperts;
import com.app.brainmap.domain.entities.ServiceListing;
import org.springframework.data.domain.Page;

import java.util.List;

public interface DomainExpertsService {

    List<DomainExperts> listDomainExperts();
    ServiceListing createServiceListing(ServiceListingRequestDto serviceListingRequestDto);
    Page<ServiceListingResponseDto> getAllServiceListings(int page, int size, String sortBy);



}
