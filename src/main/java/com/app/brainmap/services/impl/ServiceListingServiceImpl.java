package com.app.brainmap.services.impl;

import com.app.brainmap.domain.dto.DomainExpert.ServiceListingRequestDto;
import com.app.brainmap.domain.dto.DomainExpert.ServiceListingResponseDto;
import com.app.brainmap.domain.entities.DomainExpert.ServiceListing;
import com.app.brainmap.domain.entities.DomainExpert.ServiceListingAvailability;
import com.app.brainmap.domain.entities.DomainExpert.ServiceListingOffer;
import com.app.brainmap.domain.entities.User;
import com.app.brainmap.mappers.ServiceListingResponseMapper;
import com.app.brainmap.repositories.ServiceListingRepository;
import com.app.brainmap.repositories.ServiceListingAvailabilityRepository;
import com.app.brainmap.repositories.UserRepository;
import com.app.brainmap.services.FileStorageService;
import com.app.brainmap.services.ServiceListingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceListingServiceImpl implements ServiceListingService {
    private final ServiceListingRepository serviceListingRepository;
    private final UserRepository userRepository;
    private final ServiceListingAvailabilityRepository serviceListingAvailabilityRepository;
    private final ServiceListingResponseMapper serviceListingResponseMapper;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public ServiceListing createServiceListing(ServiceListingRequestDto serviceListingRequestDto, UUID mentorId) {
        User mentor = userRepository.findById(mentorId)
                .orElseThrow(() -> new RuntimeException("Mentor not found with UUID: " + mentorId));
        ServiceListing.ServiceListingBuilder builder = ServiceListing.builder()
                .title(serviceListingRequestDto.getTitle())
                .subject(serviceListingRequestDto.getSubject())
                .description(serviceListingRequestDto.getDescription())
                .hourlyRatePerPerson(serviceListingRequestDto.getHourlyRatePerPerson())
                .hourlyRatePerGroup(serviceListingRequestDto.getHourlyRatePerGroup())
                .mentor(mentor);

        if (serviceListingRequestDto.getThumbnail() != null && !serviceListingRequestDto.getThumbnail().isEmpty()) {
            String thumbnailUrl = fileStorageService.store(serviceListingRequestDto.getThumbnail(), "services/" + mentorId  + "/thumbnails");
            builder.thumbnailUrl(thumbnailUrl);
        }
        ServiceListing service = builder.build();
        List<ServiceListingAvailability> availabilities = serviceListingRequestDto.getAvailabilities().stream()
                .map(availability -> ServiceListingAvailability.builder()
                        .dayOfWeek(availability.getDayOfWeek())
                        .startTime(availability.getStartTime())
                        .endTime(availability.getEndTime())
                        .service(service)
                        .build())
                .toList();
        service.setAvailabilities(availabilities);
        if (serviceListingRequestDto.getWhatYouGet() != null && !serviceListingRequestDto.getWhatYouGet().isEmpty()) {
            List<ServiceListingOffer> offers = serviceListingRequestDto.getWhatYouGet().stream()
                    .map(dto -> ServiceListingOffer.builder()
                            .title(dto.getTitle())
                            .description(dto.getDescription())
                            .serviceListing(service)
                            .build())
                    .toList();
            service.setOffers(offers);
        }
        return serviceListingRepository.save(service);
    }

    @Override
    public Page<ServiceListingResponseDto> getAllServiceListings(int page, int size, String sortBy) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortBy));
        return serviceListingRepository.findAll(pageRequest).map(serviceListingResponseMapper::toServiceListingResponseDto);
    }

    @Override
    public ServiceListingResponseDto getServiceListingById(UUID serviceId) {
        ServiceListing service = serviceListingRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service listing not found"));
        return serviceListingResponseMapper.toServiceListingResponseDto(service);
    }

    @Override
    @Transactional
    public ServiceListingResponseDto updateServiceListing(UUID serviceId, ServiceListingRequestDto serviceListingRequestDto) {
        ServiceListing service = serviceListingRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service listing not found"));

        service.setTitle(serviceListingRequestDto.getTitle());
        service.setSubject(serviceListingRequestDto.getSubject());
        service.setDescription(serviceListingRequestDto.getDescription());
        service.setHourlyRatePerPerson(serviceListingRequestDto.getHourlyRatePerPerson());
        service.setHourlyRatePerGroup(serviceListingRequestDto.getHourlyRatePerGroup());

        if (serviceListingRequestDto.getThumbnail() != null && !serviceListingRequestDto.getThumbnail().isEmpty()) {
            String thumbnailUrl = fileStorageService.store(serviceListingRequestDto.getThumbnail(), "services/" + service.getMentor().getId() + "/thumbnails");
            service.setThumbnailUrl(thumbnailUrl);
        }

        // Update availabilities
        service.getAvailabilities().clear();
        if (serviceListingRequestDto.getAvailabilities() != null && !serviceListingRequestDto.getAvailabilities().isEmpty()) {
            List<ServiceListingAvailability> availabilities = serviceListingRequestDto.getAvailabilities().stream()
                    .map(availability -> ServiceListingAvailability.builder()
                            .dayOfWeek(availability.getDayOfWeek())
                            .startTime(availability.getStartTime())
                            .endTime(availability.getEndTime())
                            .service(service)
                            .build())
                    .toList();
            service.getAvailabilities().addAll(availabilities);
        }

        // Update offers
        service.getOffers().clear();
        if (serviceListingRequestDto.getWhatYouGet() != null && !serviceListingRequestDto.getWhatYouGet().isEmpty()) {
            List<ServiceListingOffer> offers = serviceListingRequestDto.getWhatYouGet().stream()
                    .map(dto -> ServiceListingOffer.builder()
                            .title(dto.getTitle())
                            .description(dto.getDescription())
                            .serviceListing(service)
                            .build())
                    .toList();
            service.getOffers().addAll(offers);
        }

        return serviceListingResponseMapper.toServiceListingResponseDto(serviceListingRepository.save(service));
    }

    @Override
    @Transactional
    public void deleteServiceListing(UUID serviceId) {
        serviceListingRepository.deleteById(serviceId);
    }

    @Override
    public List<ServiceListingResponseDto> getServiceListingsByMentorId(UUID mentorId) {
        List<ServiceListing> listings = serviceListingRepository.findByMentor_Id(mentorId);
        return listings.stream()
                .map(serviceListingResponseMapper::toServiceListingResponseDto)
                .toList();
    }

    /**
     * Returns the mentor's UUID for a given service listing ID.
     */
    @Override
    public UUID getMentorIdByServiceId(UUID serviceId) {
        ServiceListing service = serviceListingRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service listing not found"));
        return service.getMentor().getId();
    }

}
