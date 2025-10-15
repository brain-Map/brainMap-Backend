package com.app.brainmap.services;

import com.app.brainmap.domain.dto.DomainExpert.CompleteDomainExpertProfileDto;
import com.app.brainmap.domain.dto.DomainExpert.DomainExpertProfileDto;
import com.app.brainmap.domain.dto.DomainExpert.ServiceBookingRequestDto;
import com.app.brainmap.domain.dto.DomainExpert.ServiceBookingResponseDto;
import com.app.brainmap.domain.entities.DomainExpert.DomainExperts;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface DomainExpertsService {

    List<DomainExperts> listDomainExperts();
    DomainExpertProfileDto getDomainExpertProfile(UUID userId);
    UUID completeDomainExpertProfile(UUID id, CompleteDomainExpertProfileDto profileDto);
    Boolean isProfileComplete(UUID userId);
    ServiceBookingResponseDto createServiceBooking(ServiceBookingRequestDto requestDto, UUID userId);
    ServiceBookingResponseDto reviewServiceBooking(UUID bookingId, boolean accept, ServiceBookingRequestDto adjustmentDto, String rejectionReason, UUID expertId);
    List<ServiceBookingResponseDto> getBookingsForService(UUID serviceId);
    List<ServiceBookingResponseDto> getBookingsForUser(UUID userId);
    List<ServiceBookingResponseDto> getBookingsForDomainExpert(UUID expertId);
    List<ServiceBookingResponseDto> getBookingsForDomainExpertFiltered(UUID expertId, String status, String date);
}
