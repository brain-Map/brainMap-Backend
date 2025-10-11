package com.app.brainmap.domain.entities.DomainExpert;

import com.app.brainmap.domain.entities.User;
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
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "domain_expert_id")
    private DomainExperts domainExpert;

    private int duration;
    private String projectDetails;
    private LocalDate requestedDate;
    private LocalTime requestedStartTime;
    private LocalTime requestedEndTime;
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    private ServiceBookingStatus status;

    private LocalDate acceptedDate;
    private LocalTime acceptedTime;
    private BigDecimal acceptedPrice;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
