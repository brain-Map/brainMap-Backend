package com.app.brainmap.controllers;

import com.app.brainmap.domain.dto.*;
import com.app.brainmap.domain.dto.DomainExpert.CompleteDomainExpertProfileDto;
import com.app.brainmap.domain.dto.DomainExpert.ServiceBookingRequestDto;
import com.app.brainmap.domain.dto.DomainExpert.ServiceBookingResponseDto;
import com.app.brainmap.mappers.DomainExpertsMapper;
import com.app.brainmap.security.JwtUserDetails;
import com.app.brainmap.services.DomainExpertsService;
import com.app.brainmap.services.ServiceListingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
}
