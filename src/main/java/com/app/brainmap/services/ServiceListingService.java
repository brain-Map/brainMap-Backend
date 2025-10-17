package com.app.brainmap.services;

import com.app.brainmap.domain.dto.DomainExpert.*;
import com.app.brainmap.domain.entities.DomainExpert.DomainExperts;
import com.app.brainmap.domain.entities.DomainExpert.ServiceListing;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface ServiceListingService {
    ServiceListing createServiceListing(ServiceListingRequestDto serviceListingRequestDto, UUID mentorId);
    Page<ServiceListingResponseDto> getAllServiceListings(int page, int size, String sortBy);
    ServiceListingResponseDto getServiceListingById(UUID serviceId);
    ServiceListingResponseDto updateServiceListing(UUID serviceId, ServiceListingRequestDto serviceListingRequestDto);
    void deleteServiceListing(UUID serviceId);
    List<ServiceListingResponseDto> getServiceListingsByMentorId(UUID mentorId);
    UUID getMentorIdByServiceId(UUID serviceId);
    ServiceBookingResponseDto createServiceBooking(ServiceBookingRequestDto requestDto, UUID userId);
    ServiceBookingResponseDto reviewServiceBooking(UUID bookingId, boolean accept, ServiceBookingRequestDto adjustmentDto, String rejectionReason, UUID expertId);
    List<ServiceBookingResponseDto> getBookingsForService(UUID serviceId);
    List<ServiceBookingResponseDto> getBookingsForUser(UUID userId);
    List<ServiceBookingResponseDto> getBookingsForDomainExpert(UUID expertId);
    List<ServiceBookingResponseDto> getBookingsForDomainExpertFiltered(UUID expertId, String status, String date);
    ServiceBookingResponseDto updateServiceBooking(UUID bookingId, BookingUpdateDto updateDto, UUID userId);
}
