package com.app.brainmap.domain.dto.DomainExpert;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class BookingUpdateDto {
    private BigDecimal updatedPrice;
    private LocalTime updatedStartTime;
    private LocalTime updatedEndTime;
    private LocalDate updatedDate;
    private List<String> updatedMonths;
    private String reason;
}
