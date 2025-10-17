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

    @Override
    public List<DomainExperts> listDomainExperts() {
        return domainExpertRepository.findAll();
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
    public Boolean isProfileComplete(UUID userId) {
        return domainExpertRepository.findById(userId)
                .map(expert -> expert.getStatus() == DomainExpertStatus.VERIFIED || expert.getStatus() == DomainExpertStatus.PENDING)
                .orElse(false);
    }

    /*
    * Service Bookings
    */



}
