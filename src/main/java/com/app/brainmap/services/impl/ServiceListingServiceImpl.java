package com.app.brainmap.services.impl;

import com.app.brainmap.domain.dto.DomainExpert.*;
import com.app.brainmap.domain.entities.DomainExpert.*;
import com.app.brainmap.domain.entities.User;
import com.app.brainmap.mappers.BookingMapper;
import com.app.brainmap.mappers.ServiceListingResponseMapper;
import com.app.brainmap.repositories.DomainExpertRepository;
import com.app.brainmap.repositories.ServiceBookingRepository;
import com.app.brainmap.repositories.ServiceListingRepository;
import com.app.brainmap.repositories.UserRepository;
import com.app.brainmap.services.FileStorageService;
import com.app.brainmap.services.NotificationService;
import com.app.brainmap.services.ServiceListingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceListingServiceImpl implements ServiceListingService {
    private final ServiceListingRepository serviceListingRepository;
    private final UserRepository userRepository;
    private final ServiceListingResponseMapper serviceListingResponseMapper;
    private final FileStorageService fileStorageService;
    private final DomainExpertRepository domainExpertRepository;
    private final ServiceBookingRepository serviceBookingRepository;
    private final BookingMapper bookingMapper;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public ServiceListing createServiceListing(ServiceListingRequestDto serviceListingRequestDto, UUID mentorId) {
        User mentor = userRepository.findById(mentorId)
                .orElseThrow(() -> new RuntimeException("Mentor not found with UUID: " + mentorId));
        ServiceListing.ServiceListingBuilder builder = ServiceListing.builder()
                .title(serviceListingRequestDto.getTitle())
                .category(serviceListingRequestDto.getCategory())
                .description(serviceListingRequestDto.getDescription())
                .mentor(mentor);

        if (serviceListingRequestDto.getThumbnail() != null && !serviceListingRequestDto.getThumbnail().isEmpty()) {
            String thumbnailUrl = fileStorageService.store(serviceListingRequestDto.getThumbnail(), "services/" + mentorId  + "/thumbnails");
            builder.thumbnailUrl(thumbnailUrl);
        }
        ServiceListing service = builder.build();
        // set availabilityModes if provided
        if (serviceListingRequestDto.getAvailabilityModes() != null) {
            service.setAvailabilityModes(serviceListingRequestDto.getAvailabilityModes());
        }
        if (serviceListingRequestDto.getWhatYouGet() != null && !serviceListingRequestDto.getWhatYouGet().isEmpty()) {
            List<ServiceListingOffer> offers = serviceListingRequestDto.getWhatYouGet().stream()
                    .map(dto -> ServiceListingOffer.builder()
                            .title(dto.getTitle())
                            .description(dto.getDescription())
                            .serviceListing(service)
                            .build())
                    .toList();
            service.setOffers(offers);
        }

        // Handle pricings
        if (serviceListingRequestDto.getPricings() != null && !serviceListingRequestDto.getPricings().isEmpty()) {
            List<ServiceListingPricing> pricings = serviceListingRequestDto.getPricings().stream()
                    .map(p -> ServiceListingPricing.builder()
                            .pricingType(p.getPricingType())
                            .price(p.getPrice())
                            .serviceListing(service)
                            .build())
                    .toList();
            service.setPricings(pricings);
        }

        return serviceListingRepository.save(service);
    }

    @Override
    public Page<ServiceListingResponseDto> getAllServiceListings(int page, int size, String sortBy) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortBy));
        return serviceListingRepository.findAll(pageRequest).map(serviceListingResponseMapper::toServiceListingResponseDto);
    }

    @Override
    public ServiceListingResponseDto getServiceListingById(UUID serviceId) {
        ServiceListing service = serviceListingRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service listing not found"));
        return serviceListingResponseMapper.toServiceListingResponseDto(service);
    }

    @Override
    @Transactional
    public ServiceListingResponseDto updateServiceListing(UUID serviceId, ServiceListingRequestDto serviceListingRequestDto) {
        ServiceListing service = serviceListingRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service listing not found"));

        service.setTitle(serviceListingRequestDto.getTitle());
        service.setCategory(serviceListingRequestDto.getCategory());
        service.setDescription(serviceListingRequestDto.getDescription());

        if (serviceListingRequestDto.getThumbnail() != null && !serviceListingRequestDto.getThumbnail().isEmpty()) {
            String thumbnailUrl = fileStorageService.store(serviceListingRequestDto.getThumbnail(), "services/" + service.getMentor().getId() + "/thumbnails");
            service.setThumbnailUrl(thumbnailUrl);
        }

        // Update offers
        service.getOffers().clear();
        if (serviceListingRequestDto.getWhatYouGet() != null && !serviceListingRequestDto.getWhatYouGet().isEmpty()) {
            List<ServiceListingOffer> offers = serviceListingRequestDto.getWhatYouGet().stream()
                    .map(dto -> ServiceListingOffer.builder()
                            .title(dto.getTitle())
                            .description(dto.getDescription())
                            .serviceListing(service)
                            .build())
                    .toList();
            service.getOffers().addAll(offers);
        }

        // Update pricings
        if (service.getPricings() != null) {
            service.getPricings().clear();
        }
        if (serviceListingRequestDto.getPricings() != null && !serviceListingRequestDto.getPricings().isEmpty()) {
            List<ServiceListingPricing> pricings = serviceListingRequestDto.getPricings().stream()
                    .map(p -> ServiceListingPricing.builder()
                            .pricingType(p.getPricingType())
                            .price(p.getPrice())
                            .serviceListing(service)
                            .build())
                    .toList();
            if (service.getPricings() == null) service.setPricings(pricings);
            else service.getPricings().addAll(pricings);
        }
        // Update availability modes
        if (serviceListingRequestDto.getAvailabilityModes() != null) {
            service.setAvailabilityModes(serviceListingRequestDto.getAvailabilityModes());
        }

        return serviceListingResponseMapper.toServiceListingResponseDto(serviceListingRepository.save(service));
    }

    @Override
    @Transactional
    public void deleteServiceListing(UUID serviceId) {
        serviceListingRepository.deleteById(serviceId);
    }

    @Override
    public List<ServiceListingResponseDto> getServiceListingsByMentorId(UUID mentorId) {
        List<ServiceListing> listings = serviceListingRepository.findByMentor_Id(mentorId);
        return listings.stream()
                .map(serviceListingResponseMapper::toServiceListingResponseDto)
                .toList();
    }

    /**
     * Returns the mentor's UUID for a given service listing ID.
     */
    @Override
    public UUID getMentorIdByServiceId(UUID serviceId) {
        ServiceListing service = serviceListingRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service listing not found"));
        return service.getMentor().getId();
    }

    /*
     * Service Booking
     */

    @Override
    public ServiceBookingResponseDto createServiceBooking(ServiceBookingRequestDto requestDto, UUID userId) {
        System.out.println("Creating service booking for userId=" + userId + " with request: " + requestDto);
        ServiceListing service = serviceListingRepository.findById(requestDto.getServiceId())
                .orElseThrow(() -> new RuntimeException("Service not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // Derive the DomainExperts entity from the service mentor's user id
        DomainExperts expert = domainExpertRepository.findById(service.getMentor().getId())
                .orElseThrow(() -> new RuntimeException("Domain expert not found for service mentor"));

        // Resolve selected pricing (by id or type)
        ServiceListingPricing selectedPricing = null;
        try {
            if (requestDto.getSelectedPricingId() != null && service.getPricings() != null) {
                selectedPricing = service.getPricings().stream()
                        .filter(p -> p.getPricingId() != null && p.getPricingId().equals(requestDto.getSelectedPricingId()))
                        .findFirst().orElse(null);
            }
            if (selectedPricing == null && requestDto.getSelectedPricingType() != null && service.getPricings() != null) {
                selectedPricing = service.getPricings().stream()
                        .filter(p -> p.getPricingType() != null && p.getPricingType().equalsIgnoreCase(requestDto.getSelectedPricingType()))
                        .findFirst().orElse(null);
            }
        } catch (Exception ex) {
            // ignore resolution errors, proceed without selected pricing
            log.debug("Failed to resolve selected pricing: {}", ex.getMessage());
        }

        // Compute total price if not provided
        java.math.BigDecimal computedTotal = requestDto.getTotalPrice();
        if (computedTotal == null) {
            if (selectedPricing != null && selectedPricing.getPrice() != null) {
                java.math.BigDecimal base = java.math.BigDecimal.valueOf(selectedPricing.getPrice());
                String pricingType = selectedPricing.getPricingType() != null ? selectedPricing.getPricingType().toLowerCase() : "";
                if ("hourly".equalsIgnoreCase(pricingType)) {
                    // prefer explicit duration if provided (>0), otherwise try to infer from requested start/end times
                    int hours = requestDto.getDuration();
                    if (hours <= 0 && requestDto.getRequestedStartTime() != null && requestDto.getRequestedEndTime() != null) {
                        try {
                            long seconds = java.time.Duration.between(requestDto.getRequestedStartTime(), requestDto.getRequestedEndTime()).getSeconds();
                            hours = (int) Math.max(1.0, Math.ceil(seconds / 3600.0));
                        } catch (Exception ex) {
                            hours = 1;
                        }
                    }
                    if (hours <= 0) hours = 1;
                    base = base.multiply(java.math.BigDecimal.valueOf(hours));
                } else if ("monthly".equalsIgnoreCase(pricingType)) {
                    int months = 1;
                    if (requestDto.getRequestedMonths() != null && !requestDto.getRequestedMonths().isEmpty()) {
                        months = requestDto.getRequestedMonths().size();
                    } else if (requestDto.getDuration() > 0) {
                        months = requestDto.getDuration();
                    }
                    if (months < 1) months = 1;
                    base = base.multiply(java.math.BigDecimal.valueOf(months));
                }
                // project-based or other: use base as-is
                computedTotal = base;
            } else {
                computedTotal = java.math.BigDecimal.ZERO;
            }
        }

        // Determine booking mode
        BookingMode bookingMode = null;
        if (requestDto.getBookingMode() != null) {
            try {
                bookingMode = BookingMode.valueOf(requestDto.getBookingMode().toUpperCase());
            } catch (Exception ex) {
                log.debug("Invalid bookingMode provided: {}", requestDto.getBookingMode());
            }
        }
        if (bookingMode == null && selectedPricing != null && selectedPricing.getPricingType() != null) {
            String pt = selectedPricing.getPricingType().toLowerCase();
            if (pt.contains("hour")) bookingMode = BookingMode.HOURLY;
            else if (pt.contains("month")) bookingMode = BookingMode.MONTHLY;
            else if (pt.contains("project")) bookingMode = BookingMode.PROJECT_BASED;
        }
        // 3) infer from requestedMonths
        if (bookingMode == null && requestDto.getRequestedMonths() != null && !requestDto.getRequestedMonths().isEmpty()) {
            bookingMode = BookingMode.MONTHLY;
        }
        // 4) infer from times/duration
        if (bookingMode == null && (requestDto.getRequestedStartTime() != null || requestDto.getRequestedEndTime() != null || requestDto.getDuration() > 0)) {
            bookingMode = BookingMode.HOURLY;
        }
        // 5) infer from project details
        if (bookingMode == null && requestDto.getProjectDetails() != null && !requestDto.getProjectDetails().isEmpty()) {
            bookingMode = BookingMode.PROJECT_BASED;
        }
        // 6) fallback default
        if (bookingMode == null) bookingMode = BookingMode.HOURLY;

        ServiceBooking booking = ServiceBooking.builder()
                .service(service)
                .user(user)
                .domainExpert(expert)
                .selectedPricing(selectedPricing)
                .projectDetails(requestDto.getProjectDetails())
                .bookingMode(bookingMode)
                .requestedMonths(requestDto.getRequestedMonths())
                .projectDeadline(requestDto.getProjectDeadline())
                .requestedDate(requestDto.getRequestedDate())
                .requestedStartTime(requestDto.getRequestedStartTime())
                .requestedEndTime(requestDto.getRequestedEndTime())
                .totalPrice(computedTotal)
                .status(ServiceBookingStatus.PENDING)
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();
        booking = serviceBookingRepository.save(booking);
        return bookingMapper.toBookingResponseDto(booking);
    }

    @Override
    @Transactional
    public ServiceBookingResponseDto reviewServiceBooking(UUID bookingId, boolean accept, ServiceBookingRequestDto adjustmentDto, String rejectionReason, UUID expertId) {
        ServiceBooking booking = serviceBookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        // Only the expert who owns the service can review
        if (!booking.getService().getMentor().getId().equals(expertId)) {
            throw new RuntimeException("Not authorized to review this booking");
        }
        if (accept) {
            booking.setStatus(ServiceBookingStatus.ACCEPTED);
            if (adjustmentDto != null) {
                booking.setAcceptedDate(adjustmentDto.getRequestedDate() != null ? adjustmentDto.getRequestedDate() : booking.getRequestedDate());
                // Accepted time: use requested start time as the accepted time
                booking.setAcceptedTime(adjustmentDto.getRequestedStartTime() != null ? adjustmentDto.getRequestedStartTime() : booking.getRequestedStartTime());
                booking.setAcceptedPrice(adjustmentDto.getTotalPrice() != null ? adjustmentDto.getTotalPrice() : booking.getTotalPrice());
            } else {
                booking.setAcceptedDate(booking.getRequestedDate());
                booking.setAcceptedTime(booking.getRequestedStartTime());
                booking.setAcceptedPrice(booking.getTotalPrice());
            }
            booking.setReason(null);
        } else {
            booking.setStatus(ServiceBookingStatus.REJECTED);
            booking.setReason(rejectionReason);
        }
        booking.setUpdatedAt(java.time.LocalDateTime.now());
        booking = serviceBookingRepository.save(booking);

        // create notification to booking user about the review (accepted/rejected)
        try {
            String title = accept ? "Booking Accepted" : "Booking Rejected";
            String serviceTitle = booking.getService() != null ? booking.getService().getTitle() : "your service";
            String body;
            if (accept) {
                body = String.format("Your booking for '%s' has been accepted. Date: %s", serviceTitle,
                        booking.getAcceptedDate() != null ? booking.getAcceptedDate().toString() : booking.getRequestedDate());
            } else {
                body = String.format("Your booking for '%s' was rejected. Reason: %s", serviceTitle,
                        booking.getReason() != null ? booking.getReason() : "No reason provided");
            }
            notificationService.createNotification(booking.getUser().getId(), title, body, "BOOKING_STATUS", booking.getId().toString());
        } catch (Exception ex) {
            log.warn("Failed to create notification for booking {}: {}", bookingId, ex.getMessage());
        }

        return bookingMapper.toBookingResponseDto(booking);
    }

    @Override
    public List<ServiceBookingResponseDto> getBookingsForService(UUID serviceId) {
        List<ServiceBooking> bookings = serviceBookingRepository.findByService_ServiceId(serviceId);
        return bookings.stream().map(bookingMapper::toBookingResponseDto).toList();
    }

    @Override
    public List<ServiceBookingResponseDto> getBookingsForUser(UUID userId) {
        List<ServiceBooking> bookings = serviceBookingRepository.findByUserId(userId);
        return bookings.stream().map(bookingMapper::toBookingResponseDto).toList();
    }

    @Override
    public List<ServiceBookingResponseDto> getBookingsForDomainExpert(UUID expertId) {
        List<ServiceBooking> bookings = serviceBookingRepository.findByDomainExpert_Id(expertId);
        return bookings.stream().map(bookingMapper::toBookingResponseDto).toList();
    }

    @Override
    public List<ServiceBookingResponseDto> getBookingsForDomainExpertFiltered(UUID expertId, String status, String date) {
        List<ServiceBooking> bookings = serviceBookingRepository.findByDomainExpert_Id(expertId);
        if (status != null && !status.isEmpty()) {
            bookings = bookings.stream()
                    .filter(b -> b.getStatus() != null && b.getStatus().name().equalsIgnoreCase(status))
                    .toList();
        }
        if (date != null && !date.isEmpty()) {
            bookings = bookings.stream()
                    .filter(b -> b.getRequestedDate() != null && b.getRequestedDate().toString().equals(date))
                    .toList();
        }
        return bookings.stream().map(bookingMapper::toBookingResponseDto).toList();
    }

    @Override
    @Transactional
    public ServiceBookingResponseDto updateServiceBooking(UUID bookingId, BookingUpdateDto updateDto, UUID userId) {
        ServiceBooking booking = serviceBookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        if (!booking.getUser().getId().equals(userId)) {
            throw new RuntimeException("Not authorized to update this booking");
        }
        if (updateDto.getUpdatedDate() != null) booking.setUpdatedDate(updateDto.getUpdatedDate());
        if (updateDto.getUpdatedStartTime() != null) booking.setUpdatedStartTime(updateDto.getUpdatedStartTime());
        if (updateDto.getUpdatedEndTime() != null) booking.setUpdatedEndTime(updateDto.getUpdatedEndTime());
        if (updateDto.getUpdatedPrice() != null) booking.setUpdatedPrice(updateDto.getUpdatedPrice());
        if (updateDto.getUpdatedMonths() != null) booking.setUpdatedMonths(updateDto.getUpdatedMonths());
        if (updateDto.getReason() != null) booking.setReason(updateDto.getReason());
        booking.setStatus(ServiceBookingStatus.UPDATED);
        booking.setUpdatedAt(java.time.LocalDateTime.now());
        booking = serviceBookingRepository.save(booking);
        return bookingMapper.toBookingResponseDto(booking);
    }
}
