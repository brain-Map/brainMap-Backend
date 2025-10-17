package com.app.brainmap.domain.dto.DomainExpert;

import com.app.brainmap.domain.entities.DomainExpert.ServiceBookingStatus;
import com.app.brainmap.domain.entities.DomainExpert.SessionType;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.UUID;

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
    private LocalDate requestedDate;
    private LocalTime requestedStartTime;
    private LocalTime requestedEndTime;
    private BigDecimal totalPrice;
    private ServiceBookingStatus status;
    private SessionType sessionType;
    private LocalDate acceptedDate;
    private LocalTime acceptedTime;
    private BigDecimal acceptedPrice;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private LocalTime updatedStartTime;
    private LocalTime updatedEndTime;
    private LocalDate updatedDate;
    private BigDecimal updatedPrice;
}

