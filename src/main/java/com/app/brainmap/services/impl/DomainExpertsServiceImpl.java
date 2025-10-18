package com.app.brainmap.services.impl;

import com.app.brainmap.domain.DomainExpertStatus;
import com.app.brainmap.domain.dto.DomainExpert.*;
import com.app.brainmap.domain.entities.DomainExpert.*;
import com.app.brainmap.domain.entities.User;
import com.app.brainmap.mappers.BookingMapper;
import com.app.brainmap.repositories.*;
import com.app.brainmap.services.DomainExpertsService;
import com.app.brainmap.services.FileStorageService;
import com.app.brainmap.services.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DomainExpertsServiceImpl implements DomainExpertsService {
    private final DomainExpertRepository domainExpertRepository;
    private final UserRepository userRepository;
    private final ExpertiseAreaRepository expertiseAreaRepository;
    private final DomainExpertEducationRepository domainExpertEducationRepository;
    private final DomainExpertVerificationDocumentRepository domainExpertVerificationDocumentRepository;
    private final FileStorageService fileStorageService;

    // new dependencies used for public profile
    private final ServiceListingRepository serviceListingRepository;
    private final com.app.brainmap.mappers.ServiceListingResponseMapper serviceListingResponseMapper;
    private final ReviewRepository reviewRepository;
    private final ServiceBookingRepository serviceBookingRepository;

    @Override
    public List<DomainExperts> listDomainExperts() {
        return domainExpertRepository.findAll();
    }

    @Override
    public DomainExpertProfileDto getDomainExpertProfile(UUID id) {
        DomainExperts expert = domainExpertRepository.findById(id).orElseThrow(() -> new RuntimeException("Expert not found"));
        User user = expert.getUser();

        DomainExpertProfileDto dto = DomainExpertProfileDto.builder()
                .id(expert.getId().toString())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .email(user.getEmail())
                .phone(user.getMobileNumber())
                .dateOfBirth(user.getDateOfBirth() != null ? user.getDateOfBirth().toString() : null)
                .location(user.getCity() != null ? user.getCity() : expert.getLocation())
                .gender(user.getGender())
                .bio(user.getBio())
                .workExperience(expert.getWorkExperience())
                .linkedinProfile(expert.getLinkedinProfile())
                .portfolio(expert.getPortfolio())
                .profilePhotoUrl(expert.getProfilePhotoUrl())
                .build();

        // expertise areas
        dto.setExpertiseAreas(expert.getExpertiseAreas().stream()
                .map(a -> ExpertiseAreaDto.builder().expertise(a.getExpertise()).experience(a.getExperience()).build())
                .collect(Collectors.toList()));

        // education
        dto.setEducation(expert.getEducations().stream()
                .map(e -> EducationDto.builder().degree(e.getDegree()).school(e.getSchool()).year(e.getYear()).build())
                .collect(Collectors.toList()));

        // verification documents
        dto.setVerificationDocs(expert.getVerificationDocuments().stream()
                .map(d -> VerificationDocumentDto.builder()
                        .id(d.getId())
                        .fileName(d.getFileName())
                        .fileUrl(d.getFileUrl())
                        .contentType(d.getContentType())
                        .size(d.getSize())
                        .status(d.getStatus())
                        .uploadedAt(d.getUploadedAt())
                        .build())
                .collect(Collectors.toList()));
        return  dto;

        return dto;

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

        expert.setWorkExperience(profileDto.getWorkExperience());
        expert.setLinkedinProfile(profileDto.getLinkedinProfile());
        expert.setPortfolio(profileDto.getPortfolio());
        expert.setLocation(profileDto.getLocation());
        expert.setStatus(DomainExpertStatus.PENDING);

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

    @Override
    @Transactional
    public UUID updateDomainExpertProfile(UUID id, CompleteDomainExpertProfileDto profileDto) {
        // reuse the same behavior as completeDomainExpertProfile for now (updates same fields)
        return completeDomainExpertProfile(id, profileDto);
    }

    @Override
    public Boolean isProfileComplete(UUID userId) {
        return domainExpertRepository.findById(userId)
                .map(expert -> expert.getStatus() == DomainExpertStatus.VERIFIED || expert.getStatus() == DomainExpertStatus.PENDING)
                .orElse(false);
    }

    /*
    * Service Bookings
    */

    @Override
    @Transactional(readOnly = true)
    public DomainExpertDto getDomainExpertPublicProfile(UUID userId) {
        DomainExperts expert = domainExpertRepository.findById(userId).orElseThrow(() -> new RuntimeException("Domain expert not found"));
        User user = expert.getUser();

        // Basic fields
        DomainExpertDto dto = DomainExpertDto.builder()
                .id(expert.getId().toString())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .avatar(user.getAvatar())
                .profilePhotoUrl(expert.getProfilePhotoUrl())
                .status(expert.getStatus() != null ? expert.getStatus().toString() : null)
                .domain(expert.getDomain())
                .bio(user.getBio())
                .workExperience(expert.getWorkExperience())
                .linkedinProfile(expert.getLinkedinProfile())
                .portfolio(expert.getPortfolio())
                .location(user.getCity() != null ? user.getCity() : expert.getLocation())
                .createdAt(user.getCreatedAt())
                .build();

        // expertise areas
        dto.setExpertiseAreas(expert.getExpertiseAreas().stream()
                .map(a -> ExpertiseAreaDto.builder().expertise(a.getExpertise()).experience(a.getExperience()).build())
                .collect(Collectors.toList()));

        // education
        dto.setEducations(expert.getEducations().stream()
                .map(e -> EducationDto.builder().degree(e.getDegree()).school(e.getSchool()).year(e.getYear()).build())
                .collect(Collectors.toList()));

        // services
        List<ServiceListingResponseDto> services = serviceListingRepository.findByMentor_Id(user.getId()).stream()
                .map(serviceListingResponseMapper::toServiceListingResponseDto)
                .collect(Collectors.toList());
        dto.setServices(services);

        // social links
        dto.setSocialLinks(user.getSocialLinks().stream()
                .map(sl -> com.app.brainmap.domain.dto.UserSocialLinkDto.builder().platform(sl.getPlatform()).url(sl.getUrl()).build())
                .collect(Collectors.toList()));

        // rating & reviews
        Double avg = reviewRepository.findAverageRatingByMentorId(user.getId());
        dto.setRating(avg != null ? avg : 0.0);
        dto.setReviewsCount(reviewRepository.countByMentor_Id(user.getId()));

        // completed bookings
        long completed = serviceBookingRepository.countByDomainExpert_IdAndStatus(expert.getId(), ServiceBookingStatus.COMPLETED);
        dto.setCompletedBookingsCount(completed);

        return dto;
    }

}
