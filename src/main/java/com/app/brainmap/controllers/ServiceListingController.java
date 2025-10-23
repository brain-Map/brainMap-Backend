package com.app.brainmap.controllers;

import com.app.brainmap.domain.dto.DomainExpert.*;
import com.app.brainmap.domain.entities.DomainExpert.ServiceListing;
import com.app.brainmap.security.JwtUserDetails;
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


    @PostMapping(path = "/{userId}/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createServiceListing(
            @PathVariable UUID userId,
            @RequestPart("service") ServiceListingRequestDto serviceListingRequestDto,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail
    ) {
        log.info("Creating service listing for userId: " + userId);
        log.info("Service listing request: " + serviceListingRequestDto);
        log.info("Thumbnail: " + thumbnail);
        try {
            if (thumbnail != null) serviceListingRequestDto.setThumbnail(thumbnail);
            ServiceListing created = serviceListingService.createServiceListing(serviceListingRequestDto, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(created.getServiceId());
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

    @PutMapping(path="/{serviceId}/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateServiceListing(
            @PathVariable UUID serviceId,
            @RequestPart("service") ServiceListingRequestDto serviceListingRequestDto,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail
    ){
        try {
            if (thumbnail != null) serviceListingRequestDto.setThumbnail(thumbnail);
            ServiceListingResponseDto updatedServiceListing = serviceListingService.updateServiceListing(serviceId, serviceListingRequestDto);
            return ResponseEntity.ok(updatedServiceListing);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/{serviceId}")
    public ResponseEntity<?> deleteServiceListing(@PathVariable UUID serviceId){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            JwtUserDetails userDetails = (authentication != null && authentication.getPrincipal() instanceof JwtUserDetails)
                    ? (JwtUserDetails) authentication.getPrincipal() : null;
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not authenticated");
            }
            // Get the mentorId of the service listing
            UUID mentorId = serviceListingService.getMentorIdByServiceId(serviceId);
            if (!userDetails.getUserId().equals(mentorId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to delete this service listing");
            }
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
            ServiceBookingResponseDto booking = serviceListingService.createServiceBooking(requestDto, userDetails.getUserId());
            System.out.println("Created Booking: " + booking);
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
        log.info("_____________________________________________________________________________________________________________________--");
        log.info("Adjustment DTO: " + adjustmentDto);
        log.info("Booking ID: " + bookingId);
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            JwtUserDetails userDetails = (authentication != null && authentication.getPrincipal() instanceof JwtUserDetails)
                    ? (JwtUserDetails) authentication.getPrincipal() : null;
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not authenticated");
            }
            ServiceBookingResponseDto booking = serviceListingService.reviewServiceBooking(
                    bookingId, true, adjustmentDto, null, userDetails.getUserId());
            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
            log.info("Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.info("Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Reject a booking (with optional reason) - domain expert only
    @PutMapping("/service-booking/{bookingId}/reject")
    public ResponseEntity<?> rejectServiceBooking(
            @PathVariable UUID bookingId,
            @RequestBody RejectBookingRequestDto rejectionReason
            ) {
        try {
            log.info("Rejection Reason: " + rejectionReason.getReason());
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            JwtUserDetails userDetails = (authentication != null && authentication.getPrincipal() instanceof JwtUserDetails)
                    ? (JwtUserDetails) authentication.getPrincipal() : null;
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not authenticated");
            }
            ServiceBookingResponseDto booking = serviceListingService.reviewServiceBooking(
                    bookingId, false, null, rejectionReason.getReason(), userDetails.getUserId());
            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Update a booking
    @PutMapping("/service-booking/{bookingId}/update")
    public ResponseEntity<?> updateServiceBooking(
            @PathVariable UUID bookingId,
            @RequestBody BookingUpdateDto updateDto
    ) {
        try {
            log.info("Booking Update DTO: " + updateDto);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            JwtUserDetails userDetails = (authentication != null && authentication.getPrincipal() instanceof JwtUserDetails)
                    ? (JwtUserDetails) authentication.getPrincipal() : null;
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not authenticated");
            }
            ServiceBookingResponseDto updatedBooking = serviceListingService.updateServiceBooking(bookingId, updateDto, userDetails.getUserId());
            return ResponseEntity.ok(updatedBooking);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Get bookings for a service
    @GetMapping("/service-listings/{serviceId}/bookings")
    public ResponseEntity<List<ServiceBookingResponseDto>> getBookingsForService(@PathVariable UUID serviceId) {
        List<ServiceBookingResponseDto> bookings = serviceListingService.getBookingsForService(serviceId);
        return ResponseEntity.ok(bookings);
    }

    // Get bookings for a user
    @GetMapping("/user/{userId}/bookings")
    public ResponseEntity<List<ServiceBookingResponseDto>> getBookingsForUser(@PathVariable UUID userId) {
        List<ServiceBookingResponseDto> bookings = serviceListingService.getBookingsForUser(userId);
        return ResponseEntity.ok(bookings);
    }

    // Get service listings by mentor (domain expert) ID
    @GetMapping("/mentor/{mentorId}")
    public ResponseEntity<List<ServiceListingResponseDto>> getServiceListingsByMentorId(@PathVariable UUID mentorId) {
        List<ServiceListingResponseDto> listings = serviceListingService.getServiceListingsByMentorId(mentorId);
        return ResponseEntity.ok(listings);
    }

    // Get bookings for a domain expert (mentor)
    @GetMapping("/mentor/{mentorId}/bookings")
    public ResponseEntity<List<ServiceBookingResponseDto>> getBookingsForDomainExpert(@PathVariable UUID mentorId) {
        List<ServiceBookingResponseDto> bookings = serviceListingService.getBookingsForDomainExpert(mentorId);
        return ResponseEntity.ok(bookings);
    }

    // Get bookings for a domain expert with optional filtering by status and date
    @GetMapping("/mentor/{mentorId}/bookings/filter")
    public ResponseEntity<List<ServiceBookingResponseDto>> getBookingsForDomainExpertFiltered(
            @PathVariable UUID mentorId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String date
    ) {
        List<ServiceBookingResponseDto> bookings = serviceListingService.getBookingsForDomainExpertFiltered(mentorId, status, date);
        return ResponseEntity.ok(bookings);
    }
}
