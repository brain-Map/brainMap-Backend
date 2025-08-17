package com.app.brainmap.controllers;

import com.app.brainmap.domain.dto.AdminUserListDto;
import com.app.brainmap.domain.dto.DomainExpertsDto;
import com.app.brainmap.domain.dto.ServiceListingRequestDto;
import com.app.brainmap.domain.dto.ServiceListingResponseDto;
import com.app.brainmap.domain.entities.ServiceListing;
import com.app.brainmap.mappers.DomainExpertsMapper;
import com.app.brainmap.services.DomainExpertsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @PostMapping("/create-service-listing")
    public ResponseEntity<String> createServiceListing(@RequestBody ServiceListingRequestDto serviceListingRequestDto) {
        try {
            domainExpertsService.createServiceListing(serviceListingRequestDto);
            return ResponseEntity.ok("Created demo service listing");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating service listing: " + e.getMessage());
        }
    }

    @GetMapping("/service-listings")
    public ResponseEntity<Page<ServiceListingResponseDto>> getAllServiceListings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "serviceId") String sortBy
    ){
        Page<ServiceListingResponseDto> serviceListings = domainExpertsService.getAllServiceListings(page, size, sortBy);
        return ResponseEntity.ok()
                .header("content-type", "application/json")
                .body(serviceListings);
    }

}
