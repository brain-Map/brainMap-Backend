package com.app.brainmap.domain.entities.DomainExpert;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "Service_listing_availability")
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ServiceListingAvailability {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "service_availability_id", updatable = false, nullable = false)
    private UUID ServiceAvailabilityid;

    @Column(name = "day_of_week", nullable = false)
    private int dayOfWeek;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id")
    private ServiceListing service;
}
