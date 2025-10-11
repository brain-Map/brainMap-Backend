package com.app.brainmap.controllers;

import com.app.brainmap.domain.dto.DomainExpert.ServiceBookingRequestDto;
import com.app.brainmap.domain.dto.DomainExpert.ServiceBookingResponseDto;
import com.app.brainmap.domain.dto.DomainExpert.ServiceListingRequestDto;
import com.app.brainmap.domain.dto.DomainExpert.ServiceListingResponseDto;
import com.app.brainmap.security.JwtUserDetails;
import com.app.brainmap.services.DomainExpertsService;
import com.app.brainmap.services.ServiceListingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path="/api/v1/service-listings")
@RequiredArgsConstructor
@Slf4j
public class ServiceListingController {
    private final ServiceListingService serviceListingService;
    private final DomainExpertsService domainExpertsService;


    @PostMapping(path = "/{userId}/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createServiceListing(
            @PathVariable UUID userId,
            @RequestPart("service") ServiceListingRequestDto serviceListingRequestDto,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail
    ) {
        try {
            if (thumbnail != null) serviceListingRequestDto.setThumbnail(thumbnail);
            serviceListingService.createServiceListing(serviceListingRequestDto, userId);
            return ResponseEntity.ok("Created service listing");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating service listing: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<Page<ServiceListingResponseDto>> getAllServiceListings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "serviceId") String sortBy
    ){
        Page<ServiceListingResponseDto> serviceListings = serviceListingService.getAllServiceListings(page, size, sortBy);
        return ResponseEntity.ok().body(serviceListings);
    }

    @GetMapping("/{serviceId}")
    public ResponseEntity<?> getServiceListingById(@PathVariable UUID serviceId) {
        try {
            ServiceListingResponseDto dto = serviceListingService.getServiceListingById(serviceId);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> updateServiceListing(
            @RequestParam UUID serviceId,
            @RequestBody ServiceListingRequestDto serviceListingRequestDto
    ){
        try {
            ServiceListingResponseDto updatedServiceListing = serviceListingService.updateServiceListing(serviceId, serviceListingRequestDto);
            return ResponseEntity.ok(updatedServiceListing);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/{serviceId}")
    public ResponseEntity<?> deleteServiceListing(@PathVariable UUID serviceId){
        try {
            serviceListingService.deleteServiceListing(serviceId);
            return ResponseEntity.noContent().build();
        }catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /*
    *
    * Booking Endpoints
    *
    */

    // Create a service booking (user)
    @PostMapping("/service-booking")
    public ResponseEntity<?> createServiceBooking(@RequestBody ServiceBookingRequestDto requestDto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            JwtUserDetails userDetails = (authentication != null && authentication.getPrincipal() instanceof JwtUserDetails)
                    ? (JwtUserDetails) authentication.getPrincipal() : null;
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not authenticated");
            }
            ServiceBookingResponseDto booking = domainExpertsService.createServiceBooking(requestDto, userDetails.getUserId());
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Accept a booking (with optional adjustment) - domain expert only
    @PutMapping("/service-booking/{bookingId}/accept")
    public ResponseEntity<?> acceptServiceBooking(
            @PathVariable UUID bookingId,
            @RequestBody(required = false) ServiceBookingRequestDto adjustmentDto
    ) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            JwtUserDetails userDetails = (authentication != null && authentication.getPrincipal() instanceof JwtUserDetails)
                    ? (JwtUserDetails) authentication.getPrincipal() : null;
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not authenticated");
            }
            ServiceBookingResponseDto booking = domainExpertsService.reviewServiceBooking(
                    bookingId, true, adjustmentDto, null, userDetails.getUserId());
            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Reject a booking (with optional reason) - domain expert only
    @PutMapping("/service-booking/{bookingId}/reject")
    public ResponseEntity<?> rejectServiceBooking(
            @PathVariable UUID bookingId,
            @RequestParam(required = false) String rejectionReason
    ) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            JwtUserDetails userDetails = (authentication != null && authentication.getPrincipal() instanceof JwtUserDetails)
                    ? (JwtUserDetails) authentication.getPrincipal() : null;
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not authenticated");
            }
            ServiceBookingResponseDto booking = domainExpertsService.reviewServiceBooking(
                    bookingId, false, null, rejectionReason, userDetails.getUserId());
            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Get bookings for a service
    @GetMapping("/service-listings/{serviceId}/bookings")
    public ResponseEntity<List<ServiceBookingResponseDto>> getBookingsForService(@PathVariable UUID serviceId) {
        List<ServiceBookingResponseDto> bookings = domainExpertsService.getBookingsForService(serviceId);
        return ResponseEntity.ok(bookings);
    }

    // Get bookings for a user
    @GetMapping("/user/{userId}/bookings")
    public ResponseEntity<List<ServiceBookingResponseDto>> getBookingsForUser(@PathVariable UUID userId) {
        List<ServiceBookingResponseDto> bookings = domainExpertsService.getBookingsForUser(userId);
        return ResponseEntity.ok(bookings);
    }
}
