package com.app.brainmap.domain.dto.DomainExpert;

import com.app.brainmap.domain.entities.DomainExpert.ServiceBookingStatus;
import com.app.brainmap.domain.entities.DomainExpert.BookingMode;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

@Data
public class ServiceBookingResponseDto {
    private UUID id;
    private UUID serviceId;
    private String serviceTitle;
    private UUID userId;
    private String username;
    private String userFirstName;
    private String userLastName;
    private String userEmail;
    private String userAvatar;
    private int duration;
    private String projectDetails;
    private BookingMode bookingMode;
    private List<String> requestedMonths;
    private List<String> updatedMonths;
    private LocalDate projectDeadline;
    private LocalDate requestedDate;
    private LocalTime requestedStartTime;
    private LocalTime requestedEndTime;
    private BigDecimal totalPrice;
    private ServiceBookingStatus status;
    private LocalDate acceptedDate;
    private LocalTime acceptedTime;
    private BigDecimal acceptedPrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private LocalTime updatedStartTime;
    private LocalTime updatedEndTime;
    private LocalDate updatedDate;
    private BigDecimal updatedPrice;

    private UUID selectedPricingId;
    private String selectedPricingType;
    private Double selectedPricingPrice;
}
