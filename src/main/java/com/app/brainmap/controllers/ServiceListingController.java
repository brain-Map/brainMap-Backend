package com.app.brainmap.controllers;


import com.app.brainmap.domain.dto.ServiceListingRequestDto;
import com.app.brainmap.domain.dto.ServiceListingResponseDto;
import com.app.brainmap.mappers.DomainExpertsMapper;
import com.app.brainmap.services.DomainExpertsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path="/api/v1/service-listing")
@RequiredArgsConstructor
public class ServiceListingController {
    private final DomainExpertsService domainExpertsService;


    // domain experts service listings
    @PostMapping("/create")
    public ResponseEntity<String> createServiceListing(@RequestBody ServiceListingRequestDto serviceListingRequestDto) {
        try {
            domainExpertsService.createServiceListing(serviceListingRequestDto);
            return ResponseEntity.ok("Created demo service listing");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating service listing: " + e.getMessage());
        }
    }

    // Get all service listings
    @GetMapping("/all")
    public ResponseEntity<Page<ServiceListingResponseDto>> getAllServiceListings(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size, @RequestParam(defaultValue = "serviceId") String sortBy){Page<ServiceListingResponseDto> serviceListings = domainExpertsService.getAllServiceListings(page, size, sortBy);return ResponseEntity.ok().header("content-type", "application/json")
            .body(serviceListings);
    }

    // Get service listing by ID
    @GetMapping("/by_id")
    public ResponseEntity<?> getServiceListingById(
            @RequestParam UUID serviceId
    ) {
        try {
            ServiceListingResponseDto dto = domainExpertsService.getServiceListingById(serviceId);
            return  ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Update service listing
    @PutMapping("/update")
    public ResponseEntity<?> updateServiceListing(
            @RequestParam UUID serviceId,
            @RequestBody ServiceListingRequestDto serviceListingRequestDto
    ){
        try {
            ServiceListingResponseDto updatedServiceListing = domainExpertsService.updateServiceListing(serviceId, serviceListingRequestDto);
            return  ResponseEntity.ok(updatedServiceListing);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Delete service listing
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteServiceListing(
            @RequestParam UUID serviceId
    ){
        try {
            domainExpertsService.deleteServiceListing(serviceId);
            return ResponseEntity.noContent().build();
        }catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
