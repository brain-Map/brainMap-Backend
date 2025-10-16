package com.app.brainmap.services.impl;

import com.app.brainmap.domain.DomainExpertStatus;
import com.app.brainmap.domain.dto.DomainExpert.*;
import com.app.brainmap.domain.entities.DomainExpert.*;
import com.app.brainmap.domain.entities.User;
import com.app.brainmap.mappers.BookingMapper;
import com.app.brainmap.mappers.ServiceListingResponseMapper;
import com.app.brainmap.repositories.*;
import com.app.brainmap.services.DomainExpertsService;
import com.app.brainmap.services.FileStorageService;
import com.app.brainmap.services.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DomainExpertsServiceImpl implements DomainExpertsService {
    private final DomainExpertRepository domainExpertRepository;
    private final UserRepository userRepository;
    private final ExpertiseAreaRepository expertiseAreaRepository;
    private final DomainExpertEducationRepository domainExpertEducationRepository;
    private final DomainExpertVerificationDocumentRepository domainExpertVerificationDocumentRepository;
    private final FileStorageService fileStorageService;
    private final ServiceBookingRepository serviceBookingRepository;
    private final ServiceListingRepository serviceListingRepository;
    private final BookingMapper bookingMapper;
    private final NotificationService notificationService;

    @Override
    public List<DomainExperts> listDomainExperts() {
        return domainExpertRepository.findAll();
    }

    @Override
    public DomainExpertProfileDto getDomainExpertProfile(UUID id) {
        DomainExperts expert = domainExpertRepository.findById(id).orElseThrow(() -> new RuntimeException("Expert not found"));
        User user = expert.getUser();

        return DomainExpertProfileDto.builder()
                .id(expert.getId().toString())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
//                .status(expert.getStatus().toString())
                .build();
    }

    @Override
    @Transactional
    public UUID completeDomainExpertProfile(UUID id, CompleteDomainExpertProfileDto profileDto) {
        log.debug("Completing expert profile for id={}", id);
        DomainExperts expert = domainExpertRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Domain expert not found"));
        User user = expert.getUser();

        // Update user core data
        if (profileDto.getFirstName() != null) user.setFirstName(profileDto.getFirstName());
        if (profileDto.getLastName() != null) user.setLastName(profileDto.getLastName());
        if (profileDto.getEmail() != null) user.setEmail(profileDto.getEmail());
        if (profileDto.getPhone() != null) user.setMobileNumber(profileDto.getPhone());
        if (profileDto.getGender() != null) user.setGender(profileDto.getGender());
        if (profileDto.getBio() != null) user.setBio(profileDto.getBio());
        if (profileDto.getDateOfBirth() != null) {
            try { user.setDateOfBirth(java.sql.Date.valueOf(LocalDate.parse(profileDto.getDateOfBirth()))); } catch (Exception ex) { log.warn("DOB parse failed: {}", ex.getMessage()); }
        }
        if (profileDto.getLocation() != null) user.setCity(profileDto.getLocation());

        // Update expert extended fields
        expert.setMentorshipType(profileDto.getMentorshipType());
        expert.setAvailability(profileDto.getAvailability());
        if (profileDto.getHourlyRate() != null) {
            try { expert.setHourlyRate(new BigDecimal(profileDto.getHourlyRate())); } catch (NumberFormatException ex) { log.warn("Hourly rate parse error: {}", ex.getMessage()); }
        }
        if (profileDto.getMaxMentees() != null) {
            try { expert.setMaxMentees(Integer.parseInt(profileDto.getMaxMentees())); } catch (NumberFormatException ex) { log.warn("Max mentees parse error: {}", ex.getMessage()); }
        }
        expert.setWorkExperience(profileDto.getWorkExperience());
        expert.setLinkedinProfile(profileDto.getLinkedinProfile());
        expert.setPortfolio(profileDto.getPortfolio());
        expert.setAddress(profileDto.getAddress());
        expert.setLocation(profileDto.getLocation());
        expert.setStatus(DomainExpertStatus.PENDING);

        // Expertise Areas
        expert.getExpertiseAreas().clear();
        if (profileDto.getExpertiseAreas() != null && !profileDto.getExpertiseAreas().isEmpty()) {
            List<ExpertiseArea> areas = profileDto.getExpertiseAreas().stream().map(dto ->
                    ExpertiseArea.builder()
                            .expertise(dto.getExpertise())
                            .experience(dto.getExperience())
                            .domainExpert(expert)
                            .build()
            ).toList();
            areas.forEach(a -> expert.getExpertiseAreas().add(a));
            expertiseAreaRepository.saveAll(areas);
            log.debug("Saved {} expertise areas", areas.size());
        } else {
            log.debug("No expertise areas provided");
        }

        // Education
        expert.getEducations().clear();
        if (profileDto.getEducation() != null && !profileDto.getEducation().isEmpty()) {
            List<DomainExpertEducation> edus = profileDto.getEducation().stream().map(dto ->
                    DomainExpertEducation.builder()
                            .degree(dto.getDegree())
                            .school(dto.getSchool())
                            .year(dto.getYear())
                            .domainExpert(expert)
                            .build()
            ).toList();
            edus.forEach(e -> expert.getEducations().add(e));
            domainExpertEducationRepository.saveAll(edus);
            log.debug("Saved {} education records", edus.size());
        } else {
            log.debug("No education records provided");
        }

        // Profile photo
        if (profileDto.getProfilePhoto() != null && !profileDto.getProfilePhoto().isEmpty()) {
            String url = fileStorageService.store(profileDto.getProfilePhoto(), "experts/" + id + "/profile");
            expert.setProfilePhotoUrl(url);
            user.setAvatar(url);
            log.debug("Stored profile photo at {}", url);
        }

        // Verification documents
        expert.getVerificationDocuments().clear();
        if (profileDto.getVerificationDocs() != null && !profileDto.getVerificationDocs().isEmpty()) {
            List<DomainExpertVerificationDocument> docs = profileDto.getVerificationDocs().stream()
                    .filter(file -> file != null && !file.isEmpty())
                    .map(file -> {
                        String url = fileStorageService.store(file, "experts/" + id + "/verification");
                        return DomainExpertVerificationDocument.builder()
                                .domainExpert(expert)
                                .fileName(file.getOriginalFilename())
                                .fileUrl(url)
                                .contentType(file.getContentType())
                                .size(file.getSize())
                                .status("PENDING")
                                .build();
                    }).toList();
            docs.forEach(d -> expert.getVerificationDocuments().add(d));
            domainExpertVerificationDocumentRepository.saveAll(docs);
            log.debug("Saved {} verification documents", docs.size());
        } else {
            log.debug("No verification documents provided");
        }

        domainExpertRepository.saveAndFlush(expert);
        log.debug("Completed profile for expert id={}", expert.getId());
        return expert.getId();
    }

    @Override
    public Boolean isProfileComplete(UUID userId) {
        return domainExpertRepository.findById(userId)
                .map(expert -> expert.getStatus() == DomainExpertStatus.VERIFIED || expert.getStatus() == DomainExpertStatus.PENDING)
                .orElse(false);
    }

    /*
    * Service Bookings
    */

    @Override
    public ServiceBookingResponseDto createServiceBooking(ServiceBookingRequestDto requestDto, UUID userId) {
        ServiceListing service = serviceListingRepository.findById(requestDto.getServiceId())
                .orElseThrow(() -> new RuntimeException("Service not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        DomainExperts expert = domainExpertRepository.findById(requestDto.getDomainExpertId())
                .orElseThrow(() -> new RuntimeException("Domain expert not found"));

        ServiceBooking booking = ServiceBooking.builder()
                .service(service)
                .user(user)
                .domainExpert(expert)
                .duration(requestDto.getDuration())
                .projectDetails(requestDto.getProjectDetails())
                .requestedDate(requestDto.getRequestedDate())
                .requestedStartTime(requestDto.getRequestedStartTime())
                .requestedEndTime(requestDto.getRequestedEndTime())
                .totalPrice(requestDto.getTotalPrice())
                .status(ServiceBookingStatus.PENDING)
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .sessionType(SessionType.valueOf(requestDto.getSessionType().toUpperCase()))
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
                booking.setAcceptedTime(adjustmentDto.getRequestedStartTime() != null ? adjustmentDto.getRequestedStartTime() : booking.getRequestedStartTime());
                booking.setAcceptedTime(adjustmentDto.getRequestedEndTime() != null ? adjustmentDto.getRequestedEndTime() : booking.getRequestedEndTime());
                booking.setAcceptedPrice(adjustmentDto.getTotalPrice() != null ? adjustmentDto.getTotalPrice() : booking.getTotalPrice());
            } else {
                booking.setAcceptedDate(booking.getRequestedDate());
                booking.setAcceptedTime(booking.getAcceptedTime());
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
        if (updateDto.getReason() != null) booking.setReason(updateDto.getReason());
        booking.setStatus(ServiceBookingStatus.UPDATED);
        booking.setUpdatedAt(java.time.LocalDateTime.now());
        booking = serviceBookingRepository.save(booking);
        return bookingMapper.toBookingResponseDto(booking);
    }

}
