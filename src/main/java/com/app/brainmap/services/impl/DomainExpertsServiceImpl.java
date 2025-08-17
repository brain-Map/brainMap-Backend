package com.app.brainmap.services.impl;

import com.app.brainmap.domain.dto.AdminUserListDto;
import com.app.brainmap.domain.dto.ServiceListingRequestDto;
import com.app.brainmap.domain.dto.ServiceListingResponseDto;
import com.app.brainmap.domain.entities.DomainExperts;
import com.app.brainmap.domain.entities.ServiceListingAvailability;
import com.app.brainmap.domain.entities.ServiceListing;
import com.app.brainmap.domain.entities.User;
import com.app.brainmap.mappers.ServiceListingResponseMapper;
import com.app.brainmap.repositories.DomainExpertRepository;
import com.app.brainmap.repositories.ServiceListingAvailabilityRepository;
import com.app.brainmap.repositories.ServiceListingRepository;
import com.app.brainmap.repositories.UserRepository;
import com.app.brainmap.services.DomainExpertsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DomainExpertsServiceImpl implements DomainExpertsService {
    private final DomainExpertRepository domainExpertRepository;
    private final ServiceListingRepository serviceListingRepository;
    private final UserRepository userRepository;
    private final ServiceListingAvailabilityRepository serviceListingAvailabilityRepository;
    private final ServiceListingResponseMapper serviceListingResponseMapper;

    @Override
    public List<DomainExperts> listDomainExperts() {
        return domainExpertRepository.findAll();
    }

    // create service listing
    @Override
    public ServiceListing createServiceListing(ServiceListingRequestDto serviceListingRequestDto){
        // Fetch mentor
        User mentor = userRepository.findById(serviceListingRequestDto.getMentorId())
                .orElseThrow(() -> new RuntimeException("Mentor not found with UUID: " + serviceListingRequestDto.getMentorId()));
        // Create ServiceListing
        ServiceListing service = ServiceListing.builder()
                .title(serviceListingRequestDto.getTitle())
                .subject(serviceListingRequestDto.getSubject())
                .description(serviceListingRequestDto.getDescription())
                .fee(serviceListingRequestDto.getFee())
                .mentor(mentor)
                .build();
        // create availability
        List<ServiceListingAvailability> availabilities = serviceListingRequestDto.getAvailabilities().stream()
                .map(availability -> ServiceListingAvailability.builder()
                        .dayOfWeek(availability.getDayOfWeek())
                        .startTime(availability.getStartTime())
                        .endTime(availability.getEndTime())
                        .service(service)
                        .build())
                .toList();

        service.setAvailabilities(availabilities);
        return serviceListingRepository.save(service);
    }

    // retrieve service listing (all)
    @Override
    public Page<ServiceListingResponseDto> getAllServiceListings(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<ServiceListing> serviceListings = serviceListingRepository.findAll(pageable);
        return serviceListings.map(serviceListingResponseMapper::toServiceListingResponseDto);
    }

}
