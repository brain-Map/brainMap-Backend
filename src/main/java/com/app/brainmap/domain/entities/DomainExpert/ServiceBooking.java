package com.app.brainmap.domain.entities.DomainExpert;

import com.app.brainmap.domain.entities.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "service_bookings")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceBooking {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id")
    private ServiceListing service;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "domain_expert_id")
    @JsonIgnore
    private DomainExperts domainExpert;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_pricing_id")
    private ServiceListingPricing selectedPricing;

    private int duration;

    @Column(columnDefinition = "TEXT")
    private String projectDetails;

    @Enumerated(EnumType.STRING)
    private BookingMode bookingMode;

    // For monthly bookings: list of months requested (e.g., "2025-11", "2025-12")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "service_booking_requested_months", joinColumns = @JoinColumn(name = "booking_id"))
    @Column(name = "requested_month")
    private java.util.List<String> requestedMonths;

    // For project-based bookings: approximate ending date / deadline
    private java.time.LocalDate projectDeadline;

    // Additional structured project info
    private String projectState;

    private LocalDate requestedDate;
    private LocalTime requestedStartTime;
    private LocalTime requestedEndTime;

    private LocalDate updatedDate;
    private LocalTime updatedStartTime;
    private LocalTime updatedEndTime;
    private BigDecimal updatedPrice;

    // Months set when a booking is updated (e.g., user changes monthly selection)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "service_booking_updated_months", joinColumns = @JoinColumn(name = "booking_id"))
    @Column(name = "updated_month")
    private java.util.List<String> updatedMonths;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    private ServiceBookingStatus status;

    private LocalDate acceptedDate;
    private LocalTime acceptedTime;
    private BigDecimal acceptedPrice;
    private String reason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
