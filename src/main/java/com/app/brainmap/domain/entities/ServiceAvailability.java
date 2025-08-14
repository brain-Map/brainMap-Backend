package com.app.brainmap.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

import java.util.UUID;

@Entity
@Table(name = "service_availability")
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ServiceAvailability {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "service_availability_id", updatable = false, nullable = false)
    private UUID ServiceAvailabilityid;

    @Column(name = "day_of_week", nullable = false)
    private String dayOfWeek;

    @Column(name = "start_time", nullable = false)
    private String startTime;

    @Column(name = "end_time", nullable = false)
    private String endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;
}
