package com.app.brainmap.services.impl;

import com.app.brainmap.domain.dto.DomainExpert.CompleteDomainExpertProfileDto;
import com.app.brainmap.domain.dto.DomainExpert.DomainExpertProfileDto;
import com.app.brainmap.domain.dto.ServiceListingRequestDto;
import com.app.brainmap.domain.dto.ServiceListingResponseDto;
import com.app.brainmap.domain.entities.User;
import com.app.brainmap.domain.entities.ServiceListing;
import com.app.brainmap.domain.entities.ServiceListingAvailability;
import com.app.brainmap.domain.entities.DomainExpert.DomainExperts;
import com.app.brainmap.domain.entities.DomainExpert.ExpertiseArea;
import com.app.brainmap.domain.entities.DomainExpert.DomainExpertEducation;
import com.app.brainmap.domain.entities.DomainExpert.DomainExpertVerificationDocument;
import com.app.brainmap.mappers.ServiceListingResponseMapper;
import com.app.brainmap.repositories.*;
import com.app.brainmap.services.DomainExpertsService;
import com.app.brainmap.services.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DomainExpertsServiceImpl implements DomainExpertsService {
    private final DomainExpertRepository domainExpertRepository;
    private final ServiceListingRepository serviceListingRepository;
    private final UserRepository userRepository;
    private final ServiceListingAvailabilityRepository serviceListingAvailabilityRepository; // kept if needed elsewhere
    private final ServiceListingResponseMapper serviceListingResponseMapper;
    private final ExpertiseAreaRepository expertiseAreaRepository;
    private final DomainExpertEducationRepository domainExpertEducationRepository;
    private final DomainExpertVerificationDocumentRepository domainExpertVerificationDocumentRepository;
    private final FileStorageService fileStorageService;

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

    @Override
    public ServiceListingResponseDto getServiceListingById(UUID serviceId) {
        ServiceListing serviceListing = serviceListingRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found with UUID: " + serviceId));
        return serviceListingResponseMapper.toServiceListingResponseDto(serviceListing);
    }

    @Override
    public ServiceListingResponseDto updateServiceListing(UUID serviceId, ServiceListingRequestDto serviceListingRequestDto){
        ServiceListing serviceListing = serviceListingRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service listing not found"));

        serviceListing.setTitle(serviceListingRequestDto.getTitle());
        serviceListing.setSubject(serviceListingRequestDto.getSubject());
        serviceListing.setDescription(serviceListingRequestDto.getDescription());
        serviceListing.setFee(serviceListingRequestDto.getFee());
        serviceListing.setUpdatedAt(LocalDateTime.now());

        // update availabilities
        serviceListing.getAvailabilities().clear();
        List<ServiceListingAvailability> newAvailabilities = serviceListingRequestDto.getAvailabilities().stream()
                .map(availability -> ServiceListingAvailability.builder()
                        .dayOfWeek(availability.getDayOfWeek())
                        .startTime(availability.getStartTime())
                        .endTime(availability.getEndTime())
                        .service(serviceListing)
                        .build()
                ).toList();
        serviceListing.getAvailabilities().addAll(newAvailabilities);

        ServiceListing updatedServicelisting =  serviceListingRepository.save(serviceListing);
        return serviceListingResponseMapper.toServiceListingResponseDto(updatedServicelisting);
    }

    @Override
    public  void deleteServiceListing(UUID serviceId) {
        if(!serviceListingRepository.existsById(serviceId)){
            throw new RuntimeException("Service not fount with UUID: " + serviceId);
        }
        serviceListingRepository.deleteById(serviceId);
    }

    @Override
    public DomainExpertProfileDto getDomainExpertProfile(UUID id) {
        DomainExperts expert = domainExpertRepository.findById(id).orElseThrow(() -> new RuntimeException("Expert not found"));
        User user = expert.getUser();

        return DomainExpertProfileDto.builder()
                .id(expert.getId().toString())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
//                .status(expert.getStatus().toString())
                .build();
    }

    @Override
    @Transactional
    public UUID completeDomainExpertProfile(UUID id, CompleteDomainExpertProfileDto profileDto) {
        log.debug("Completing expert profile for id={}", id);
        DomainExperts expert = domainExpertRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Domain expert not found"));
        User user = expert.getUser();

        // Update user core data
        if (profileDto.getFirstName() != null) user.setFirstName(profileDto.getFirstName());
        if (profileDto.getLastName() != null) user.setLastName(profileDto.getLastName());
        if (profileDto.getEmail() != null) user.setEmail(profileDto.getEmail());
        if (profileDto.getPhone() != null) user.setMobileNumber(profileDto.getPhone());
        if (profileDto.getGender() != null) user.setGender(profileDto.getGender());
        if (profileDto.getBio() != null) user.setBio(profileDto.getBio());
        if (profileDto.getDateOfBirth() != null) {
            try { user.setDateOfBirth(java.sql.Date.valueOf(LocalDate.parse(profileDto.getDateOfBirth()))); } catch (Exception ex) { log.warn("DOB parse failed: {}", ex.getMessage()); }
        }
        if (profileDto.getLocation() != null) user.setCity(profileDto.getLocation());

        // Update expert extended fields
        expert.setMentorshipType(profileDto.getMentorshipType());
        expert.setAvailability(profileDto.getAvailability());
        if (profileDto.getHourlyRate() != null) {
            try { expert.setHourlyRate(new BigDecimal(profileDto.getHourlyRate())); } catch (NumberFormatException ex) { log.warn("Hourly rate parse error: {}", ex.getMessage()); }
        }
        if (profileDto.getMaxMentees() != null) {
            try { expert.setMaxMentees(Integer.parseInt(profileDto.getMaxMentees())); } catch (NumberFormatException ex) { log.warn("Max mentees parse error: {}", ex.getMessage()); }
        }
        expert.setWorkExperience(profileDto.getWorkExperience());
        expert.setLinkedinProfile(profileDto.getLinkedinProfile());
        expert.setPortfolio(profileDto.getPortfolio());
        expert.setAddress(profileDto.getAddress());
        expert.setLocation(profileDto.getLocation());

        // Expertise Areas
        expert.getExpertiseAreas().clear();
        if (profileDto.getExpertiseAreas() != null && !profileDto.getExpertiseAreas().isEmpty()) {
            List<ExpertiseArea> areas = profileDto.getExpertiseAreas().stream().map(dto ->
                    ExpertiseArea.builder()
                            .expertise(dto.getExpertise())
                            .experience(dto.getExperience())
                            .domainExpert(expert)
                            .build()
            ).toList();
            areas.forEach(a -> expert.getExpertiseAreas().add(a));
            expertiseAreaRepository.saveAll(areas);
            log.debug("Saved {} expertise areas", areas.size());
        } else {
            log.debug("No expertise areas provided");
        }

        // Education
        expert.getEducations().clear();
        if (profileDto.getEducation() != null && !profileDto.getEducation().isEmpty()) {
            List<DomainExpertEducation> edus = profileDto.getEducation().stream().map(dto ->
                    DomainExpertEducation.builder()
                            .degree(dto.getDegree())
                            .school(dto.getSchool())
                            .year(dto.getYear())
                            .domainExpert(expert)
                            .build()
            ).toList();
            edus.forEach(e -> expert.getEducations().add(e));
            domainExpertEducationRepository.saveAll(edus);
            log.debug("Saved {} education records", edus.size());
        } else {
            log.debug("No education records provided");
        }

        // Profile photo
        if (profileDto.getProfilePhoto() != null && !profileDto.getProfilePhoto().isEmpty()) {
            String url = fileStorageService.store(profileDto.getProfilePhoto(), "experts/" + id + "/profile");
            expert.setProfilePhotoUrl(url);
            user.setAvatar(url);
            log.debug("Stored profile photo at {}", url);
        }

        // Verification documents
        expert.getVerificationDocuments().clear();
        if (profileDto.getVerificationDocs() != null && !profileDto.getVerificationDocs().isEmpty()) {
            List<DomainExpertVerificationDocument> docs = profileDto.getVerificationDocs().stream()
                    .filter(file -> file != null && !file.isEmpty())
                    .map(file -> {
                        String url = fileStorageService.store(file, "experts/" + id + "/verification");
                        return DomainExpertVerificationDocument.builder()
                                .domainExpert(expert)
                                .fileName(file.getOriginalFilename())
                                .fileUrl(url)
                                .contentType(file.getContentType())
                                .size(file.getSize())
                                .status("PENDING")
                                .build();
                    }).toList();
            docs.forEach(d -> expert.getVerificationDocuments().add(d));
            domainExpertVerificationDocumentRepository.saveAll(docs);
            log.debug("Saved {} verification documents", docs.size());
        } else {
            log.debug("No verification documents provided");
        }

        domainExpertRepository.saveAndFlush(expert);
        log.debug("Completed profile for expert id={}", expert.getId());
        return expert.getId();
    }
}
