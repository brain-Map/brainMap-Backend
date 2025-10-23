package com.app.brainmap.services;

import com.app.brainmap.domain.dto.DomainExpert.*;
import com.app.brainmap.domain.entities.DomainExpert.DomainExperts;

import java.util.List;
import java.util.UUID;

public interface DomainExpertsService {

    List<DomainExperts> listDomainExperts();
    DomainExpertProfileDto getDomainExpertProfile(UUID userId);
    UUID completeDomainExpertProfile(UUID id, CompleteDomainExpertProfileDto profileDto);
    UUID updateDomainExpertProfile(UUID id, CompleteDomainExpertProfileDto profileDto);
    Boolean isProfileComplete(UUID userId);
    DomainExpertDto getDomainExpertPublicProfile(UUID userId);

    VerificationDocumentDto getVerificationDocument(UUID expertId, UUID documentId);
    java.util.List<VerificationDocumentDto> getAllVerificationDocuments(UUID expertId);
    VerificationDocumentDto resubmitVerificationDocument(UUID expertId, UUID documentId, org.springframework.web.multipart.MultipartFile file);

}
