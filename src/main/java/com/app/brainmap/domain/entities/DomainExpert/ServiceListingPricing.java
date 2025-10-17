package com.app.brainmap.domain.entities.DomainExpert;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "service_listing_pricing")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ServiceListingPricing {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "pricing_id", updatable = false, nullable = false)
    private UUID pricingId;

    @Column(name = "pricing_type", nullable = false)
    private String pricingType; // e.g., hourly, monthly, project-based, yearly

    @Column(name = "price", nullable = false)
    private Double price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id")
    private ServiceListing serviceListing;

}

