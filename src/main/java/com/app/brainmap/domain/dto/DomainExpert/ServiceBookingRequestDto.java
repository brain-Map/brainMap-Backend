package com.app.brainmap.domain.dto.DomainExpert;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class ServiceBookingRequestDto {
    private UUID serviceId;
    private int duration;
    private String projectDetails;
    private LocalDate requestedDate;
    private LocalTime requestedStartTime;
    private LocalTime requestedEndTime;
    private BigDecimal totalPrice;
    private UUID domainExpertId;
}

