package com.app.brainmap.controllers;

import com.app.brainmap.domain.dto.*;
import com.app.brainmap.domain.dto.DomainExpert.CompleteDomainExpertProfileDto;
import com.app.brainmap.domain.dto.DomainExpert.DomainExpertProfileDto;
import com.app.brainmap.mappers.DomainExpertsMapper;
import com.app.brainmap.services.DomainExpertsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
public class DomainExpertsController {

    private final DomainExpertsService domainExpertsService;
    private final DomainExpertsMapper domainExpertsMapper;

    @GetMapping
    public List<DomainExpertsDto> listDomainExperts() {
        return domainExpertsService.listDomainExperts()
                .stream()
                .map(domainExpertsMapper::toDto)
                .collect(Collectors.toList());
    }

//    @GetMapping(path = "/{id}/profile")
//    public ResponseEntity<DomainExpertProfileDto> getDomainExpertProfile(@PathVariable UUID id) {
//        System.out.println("Hit the function with id: " + id);
//        try{
//            DomainExpertProfileDto expert = domainExpertsService.getDomainExpertProfile(id);
//            System.out.println("Expert: " + expert);
//            return ResponseEntity.ok(expert);
//        } catch (RuntimeException e) {
//            System.out.println("Error: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//        } catch (Exception e) {
//            System.out.println("Error: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//
//        }
//
//    }
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
}
