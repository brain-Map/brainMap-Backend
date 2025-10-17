package com.app.brainmap.domain.dto.DomainExpert;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class ServiceBookingRequestDto {
    private UUID serviceId;
    private String projectDetails;
    private String bookingMode; // "HOURLY", "MONTHLY", "PROJECT_BASED"
    private java.util.List<String> requestedMonths;
    private java.time.LocalDate projectDeadline;
    private String projectState;
    private String mentoringReason;
    private LocalDate requestedDate;
    private LocalTime requestedStartTime;
    private LocalTime requestedEndTime;
    private BigDecimal totalPrice;
    private UUID domainExpertId;
    private String sessionType;
    private int duration;

    private UUID selectedPricingId;
    private String selectedPricingType; // e.g., "hourly", "monthly", "project-based"
}
