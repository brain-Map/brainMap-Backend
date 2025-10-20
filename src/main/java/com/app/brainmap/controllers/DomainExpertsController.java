package com.app.brainmap.controllers;

import com.app.brainmap.domain.dto.*;
import com.app.brainmap.domain.dto.DomainExpert.*;
import com.app.brainmap.mappers.DomainExpertsMapper;
import com.app.brainmap.services.DomainExpertsService;
import com.app.brainmap.services.ServiceListingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
    @RequestMapping(path="/api/v1/domain-experts")
@RequiredArgsConstructor
@Slf4j
public class DomainExpertsController {

    private final DomainExpertsService domainExpertsService;
    private final DomainExpertsMapper domainExpertsMapper;
    private final ServiceListingService serviceListingService;

    @GetMapping
    public List<DomainExpertsDto> listDomainExperts() {
        return domainExpertsService.listDomainExperts()
                .stream()
                .map(domainExpertsMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping(path = "/{id}/profile-status")
    public ResponseEntity<Boolean> getProfileStatus(@PathVariable UUID id) {
        try{
            Boolean isComplete = domainExpertsService.isProfileComplete(id);
            System.out.println("Isconplete: " + isComplete);
            return ResponseEntity.ok(isComplete);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        }
    }

    @PostMapping(path = "/{id}/profile-complete", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UUID> completeDomainExpertProfile(
            @PathVariable UUID id,
            @RequestPart("profile") CompleteDomainExpertProfileDto profileDto,
            @RequestPart(value = "profilePhoto", required = false) MultipartFile profilePhoto,
            @RequestPart(value = "verificationDocs", required = false) List<MultipartFile> verificationDocs
    ) {
        try {
            if (profilePhoto != null) profileDto.setProfilePhoto(profilePhoto);
            if (verificationDocs != null) profileDto.setVerificationDocs(verificationDocs);
            UUID savedId = domainExpertsService.completeDomainExpertProfile(id, profileDto);
            return ResponseEntity.ok(savedId);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PUT endpoint to update an existing domain expert profile (same payload as profile-complete)
    @PutMapping(path = "/{id}/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UUID> updateDomainExpertProfile(
            @PathVariable UUID id,
            @RequestPart("profile") CompleteDomainExpertProfileDto profileDto,
            @RequestPart(value = "profilePhoto", required = false) MultipartFile profilePhoto,
            @RequestPart(value = "verificationDocs", required = false) List<MultipartFile> verificationDocs
    ) {
        try {
            if (profilePhoto != null) profileDto.setProfilePhoto(profilePhoto);
            if (verificationDocs != null) profileDto.setVerificationDocs(verificationDocs);
            UUID updatedId = domainExpertsService.updateDomainExpertProfile(id, profileDto);
            return ResponseEntity.ok(updatedId);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{expertId}/bookings")
    public ResponseEntity<List<ServiceBookingResponseDto>> getAllBookingsForDomainExpert(@PathVariable UUID expertId) {
        log.info("DOmainExper id" + expertId);
        List<ServiceBookingResponseDto> bookings = serviceListingService.getBookingsForDomainExpert(expertId);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/{expertId}/bookings/filter")
    public ResponseEntity<List<ServiceBookingResponseDto>> getFilteredBookingsForDomainExpert(
            @PathVariable UUID expertId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String date) {
        List<ServiceBookingResponseDto> bookings = serviceListingService.getBookingsForDomainExpertFiltered(expertId, status, date);
        return ResponseEntity.ok(bookings);
    }

    // New endpoint: public profile
    @GetMapping("/{id}/public-profile")
    public ResponseEntity<DomainExpertDto> getPublicProfile(@PathVariable UUID id) {
        try {
            DomainExpertDto dto = domainExpertsService.getDomainExpertPublicProfile(id);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get full domain expert profile
    @GetMapping(path = "/{id}/profile")
    public ResponseEntity<DomainExpertProfileDto> getDomainExpertProfile(@PathVariable UUID id) {
        try {
            DomainExpertProfileDto dto = domainExpertsService.getDomainExpertProfile(id);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(path = "/{expertId}/verification-documents/{documentId}")
    public ResponseEntity<VerificationDocumentDto> getVerificationDocument(
            @PathVariable UUID expertId,
            @PathVariable UUID documentId
    ) {
        try {
            VerificationDocumentDto dto = domainExpertsService.getVerificationDocument(expertId, documentId);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(path = "/{expertId}/verification-documents")
    public ResponseEntity<List<VerificationDocumentDto>> getAllVerificationDocuments(@PathVariable UUID expertId) {
        try {
            List<VerificationDocumentDto> docs = domainExpertsService.getAllVerificationDocuments(expertId);
            return ResponseEntity.ok(docs);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(path = "/{expertId}/verification-documents/{documentId}/resubmit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<VerificationDocumentDto> resubmitVerificationDocument(
            @PathVariable UUID expertId,
            @PathVariable UUID documentId,
            @RequestPart("file") MultipartFile file
    ) {
        try {
            VerificationDocumentDto dto = domainExpertsService.resubmitVerificationDocument(expertId, documentId, file);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
