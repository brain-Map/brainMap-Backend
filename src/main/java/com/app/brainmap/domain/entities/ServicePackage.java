package com.app.brainmap.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "service_packages")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ServicePackage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID packageId;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    private String packageName;

    @Column(columnDefinition = "TEXT")
    private String packageDescription;

    private BigDecimal price;

    private int durationDays;
    private int hoursPerDay;
}
